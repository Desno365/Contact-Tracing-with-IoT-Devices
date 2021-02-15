package it.polimi.middleware.project1.server;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import it.polimi.middleware.project1.messages.ContactMessage;
import it.polimi.middleware.project1.messages.EventOfInterestAckMessage;
import it.polimi.middleware.project1.messages.EventOfInterestReportMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

public class ServerActor extends AbstractActor {

	private final String mqttBroker;
	private final String mqttTopic;
	private final Contacts contacts = new Contacts();

	private IMqttClient mqttProducer;

	public ServerActor(String mqttBroker, String mqttTopic) {
		this.mqttBroker = mqttBroker;
		this.mqttTopic = mqttTopic;
		connectToMqttBroker();
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(ContactMessage.class, this::onContactMessage)
				.match(EventOfInterestReportMessage.class, this::onEventOfInterestReportMessage)
				.build();
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

			System.out.println("Connecting to broker: " + mqttBroker);
			mqttSubscriber.connect(options);
			mqttProducer.connect(options);
			System.out.println("Connected");

			System.out.println("Subscribing to messages of topic \"" + mqttTopic + "\".");
			mqttSubscriber.subscribe(mqttTopic, MqttUtils.DEFAULT_QOS, (String topic, MqttMessage mqttMessage) -> {
				final String stringPayload = new String(mqttMessage.getPayload());
				System.out.println("MQTT message received: topic \"" + topic + "\"; payload: \"" + stringPayload + "\".");

				try {
					final JSONObject jsonObject = new JSONObject(stringPayload);
					final int myId = jsonObject.getJSONObject("contact").getInt("myId");
					final int otherId = jsonObject.getJSONObject("contact").getInt("otherId");

					self().tell(new ContactMessage(myId, otherId), ActorRef.noSender());
				} catch(Exception err) {
					System.out.println("Failed to process json, error: " + err.toString());
				}
			});
			System.out.println("Subscribed.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	private void onContactMessage(ContactMessage msg) {
		System.out.println("Contact message received: myId " + msg.getMyId() + ", otherId: " + msg.getOtherId() + ".");

		if(msg.getMyId() == msg.getOtherId()) {
			System.out.println("Device in contact with itself, ignored.");
			return;
		}

		contacts.addContact(msg.getMyId(), msg.getOtherId());

		// To debug we print the contacts each time. (TODO remove)
		contacts.printContacts();
	}

	private void onEventOfInterestReportMessage(EventOfInterestReportMessage msg) {
		System.out.println("Event of interest report received: affectedId " + msg.getAffectedId() + ".");

		final Map<Integer, Long> timestampOfContactsOfSingleDevice = contacts.getTimestampOfContactsOfSingleDevice(msg.getAffectedId());
		System.out.println("Devices entered in contact with " + msg.getAffectedId() + " are " + timestampOfContactsOfSingleDevice.keySet().toString() + ".");

		for(Map.Entry<Integer, Long> entry : timestampOfContactsOfSingleDevice.entrySet())
			sendNotificationToDevice(entry.getKey(), entry.getValue());

		sender().tell(new EventOfInterestAckMessage(msg.getAffectedId()), self());
	}

	private void sendNotificationToDevice(int deviceId, long timestampOfContact) {
		final String messageContent = getNotificationMessageContent(deviceId, timestampOfContact);
		final String topic = MqttUtils.getNotificationTopicForDevice(deviceId);
		try {
			System.out.println("Publishing message: \"" + messageContent + "\" to topic \"" + topic + "\".");
			final MqttMessage message = new MqttMessage(messageContent.getBytes());
			message.setQos(MqttUtils.DEFAULT_QOS);
			mqttProducer.publish(topic, message);
			System.out.println("Message published.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	private String getNotificationMessageContent(int deviceId, long timestampOfContact) {
		return "{\"notification\":{\"deviceId\":\"" + deviceId + "\",\"timestampOfContact\":\"" + timestampOfContact + "\"}}";
	}
	//endregion
}
