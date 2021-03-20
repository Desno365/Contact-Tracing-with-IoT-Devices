package it.polimi.middleware.project1.server;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import it.polimi.middleware.project1.messages.ContactMessage;
import it.polimi.middleware.project1.messages.EventOfInterestAckMessage;
import it.polimi.middleware.project1.messages.EventOfInterestReportMessage;
import it.polimi.middleware.project1.messages.RequestSimulatedCrashMessage;
import it.polimi.middleware.project1.server.datastructures.Contacts;
import it.polimi.middleware.project1.utils.AkkaUtils;
import it.polimi.middleware.project1.utils.MqttUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * This class represents the server actor in a certain region.
 * The server actor of a certain region is able to receive the contacts of devices assigned to that region,
 * and is also able to send notifications to devices when a device they entered in contact with had an event of interest.
 */
public class ServerActor extends AbstractActor {

	private static final boolean LOAD_FROM_DISK = true;
	private static final boolean DEBUG_CONTACTS_CONTENT = true;

	private final String mqttBroker;
	private final String region;
	private final ActorRef mediatorActorRef;

	// State.
	private Contacts contacts = new Contacts();

	// MQTT.
	private IMqttClient mqttProducer;

	public ServerActor(String mqttBroker, String region) {
		this.mqttBroker = mqttBroker;
		this.region = region;
		this.mediatorActorRef = DistributedPubSub.get(getContext().getSystem()).mediator();
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(DistributedPubSubMediator.SubscribeAck.class, this::onAkkaPubSubSubscribeAckMessage)
				.match(ContactMessage.class, this::onContactMessage)
				.match(EventOfInterestReportMessage.class, this::onEventOfInterestReportMessage)
				.match(RequestSimulatedCrashMessage.class, this::onRequestSimulatedCrashMessage)
				.build();
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		log("Preparing to start...");

		connectToMqttBroker();
		subscribeToEventOfInterestTopic();
		if(LOAD_FROM_DISK)
			loadState();
	}

	@Override
	public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
		super.preRestart(reason, message);
		log("Preparing to restart...");
	}

	@Override
	public void postRestart(Throwable reason) throws Exception {
		super.postRestart(reason);
		log("...now restarted!");
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		log("Stopped actor.");
	}


	// ##############################################################
	//region Private methods
	// ###############################

	private void connectToMqttBroker() {
		// Simulate the IOT devices as actors by sending instances of ContactMessage to itself.
		try {
			final MemoryPersistence subscriberPersistence = new MemoryPersistence();
			final String subscriberId = UUID.randomUUID().toString();
			final MqttClient mqttSubscriber = new MqttClient(mqttBroker, subscriberId, subscriberPersistence);
			final MemoryPersistence producerPersistence = new MemoryPersistence();
			final String producerId = UUID.randomUUID().toString();
			mqttProducer = new MqttClient(mqttBroker, producerId, producerPersistence);

			final MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);

			log("MQTT: Connecting to broker: " + mqttBroker);
			mqttSubscriber.connect(options);
			mqttProducer.connect(options);
			log("MQTT: Connected.");

			final String contactTopic = MqttUtils.getContactTopicForRegion(region);
			log("MQTT: Subscribing to messages of topic \"" + contactTopic + "\".");
			mqttSubscriber.subscribe(contactTopic, MqttUtils.DEFAULT_QOS, (String topic, MqttMessage mqttMessage) -> {
				final String stringPayload = new String(mqttMessage.getPayload());
				log("MQTT: message received: topic \"" + topic + "\"; payload: \"" + stringPayload + "\".");

				try {
					final JSONObject jsonObject = new JSONObject(stringPayload);
					final String myId = jsonObject.getJSONObject("contact").getString("myId");
					final String otherId = jsonObject.getJSONObject("contact").getString("otherId");

					self().tell(new ContactMessage(myId, otherId), ActorRef.noSender());
				} catch(Exception err) {
					log("Failed to process json, error: " + err.toString());
				}
			});
			log("MQTT: Subscribed.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	private void subscribeToEventOfInterestTopic() {
		final String topic = AkkaUtils.getEventOfInterestTopicForRegion(region);
		log("Akka Pub/Sub: Subscribing to messages of topic \"" + topic + "\".");
		mediatorActorRef.tell(new DistributedPubSubMediator.Subscribe(topic, getSelf()), getSelf());
	}

	void onAkkaPubSubSubscribeAckMessage(DistributedPubSubMediator.SubscribeAck msg) {
		log("Akka Pub/Sub: Subscribed.");
	}

	private void onContactMessage(ContactMessage msg) {
		log("Contact message received: myId " + msg.myId + ", otherId: " + msg.otherId + ".");

		if(msg.myId.equals(msg.otherId)) {
			log("Device in contact with itself, ignored.");
			return;
		}

		contacts.addContact(msg.myId, msg.otherId);

		saveState();
	}

	private void loadState() {
		try {
			String contactsJsonString = Files.readString(Path.of("./servers-state/" + region + ".json"));
			contacts = new Contacts(contactsJsonString);
			log("State loaded from file.");

			if(DEBUG_CONTACTS_CONTENT)
				contacts.printContacts();
		} catch (FileNotFoundException | NoSuchFileException e) {
			log("No state found.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveState() {
		if(DEBUG_CONTACTS_CONTENT)
			contacts.printContacts();

		final String contactsJsonString = contacts.toJson();
		try {
			new File("./servers-state/").mkdirs();
			Files.writeString(Path.of("./servers-state/" + region + ".json"), contactsJsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void onEventOfInterestReportMessage(EventOfInterestReportMessage msg) {
		log("Event of interest report received: affectedId " + msg.affectedId + ".");

		final Map<String, Long> timestampOfContactsOfSingleDevice = contacts.getTimestampOfContactsOfAffectedDevice(msg.affectedId);
		log("Devices entered in contact with " + msg.affectedId + " are " + timestampOfContactsOfSingleDevice.keySet().toString() + ".");

		for(Map.Entry<String, Long> entry : timestampOfContactsOfSingleDevice.entrySet())
			sendNotificationToDevice(entry.getKey(), entry.getValue());

		sender().tell(new EventOfInterestAckMessage(msg.affectedId, msg.region), self());
	}

	private void sendNotificationToDevice(String deviceId, long timestampOfContact) {
		final String messageContent = getNotificationMessageContent(deviceId, timestampOfContact);
		final String topic = MqttUtils.getNotificationTopicForDevice(deviceId);
		try {
			log("Publishing message: \"" + messageContent + "\" to topic \"" + topic + "\".");
			final MqttMessage message = new MqttMessage(messageContent.getBytes());
			message.setQos(MqttUtils.DEFAULT_QOS);
			mqttProducer.publish(topic, message);
			log("Message published.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	private String getNotificationMessageContent(String deviceId, long timestampOfContact) {
		return "{\"notification\":{\"deviceId\":\"" + deviceId + "\",\"timestampOfContact\":\"" + timestampOfContact + "\"}}";
	}

	private void onRequestSimulatedCrashMessage(RequestSimulatedCrashMessage msg) {
		log("Request of simulated crash received.");
		log("Crash " + 2/0 + ".");
	}

	private void log(String message) {
		System.out.println(region + ": " + message);
	}
	//endregion
}
