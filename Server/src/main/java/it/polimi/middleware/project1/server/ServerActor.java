package it.polimi.middleware.project1.server;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import it.polimi.middleware.project1.messages.ContactMessage;
import it.polimi.middleware.project1.messages.EventOfInterestAckMessage;
import it.polimi.middleware.project1.messages.EventOfInterestReportMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerActor extends AbstractActor {

	private final ServiceKey<ServerActor> serverActorKey;
	private final String mqttBroker;
	private final String mqttTopic;
	private final HashMap<Integer, ContactsOfSingleDevice> contacts = new HashMap<>();

	public ServerActor(String mqttBroker, String mqttTopic) {
		this.serverActorKey = ServiceKey.create(ServerActor.class, "ServerActorId");
		this.mqttBroker = mqttBroker;
		this.mqttTopic = mqttTopic;
		subscribeToMqttMessages();
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

	private void subscribeToMqttMessages() {
		// Simulate the IOT devices as actors by sending instances of ContactMessage to itself.
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			String subscriberId = UUID.randomUUID().toString();
			MqttClient subscriber = new MqttClient(mqttBroker, subscriberId, persistence);

			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);

			System.out.println("Connecting to broker: " + mqttBroker);
			subscriber.connect(options);
			System.out.println("Connected");

			System.out.println("Subscribing to messages.");
			subscriber.subscribe(mqttTopic, MqttUtils.DEFAULT_QOS, (String topic, MqttMessage mqttMessage) -> {
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

		// Two directional contacts since the contact is bidirectional.
		final long timestamp = System.currentTimeMillis();
		addDirectionalContact(msg.getMyId(), msg.getOtherId(), timestamp);
		addDirectionalContact(msg.getOtherId(), msg.getMyId(), timestamp);

		debugPrintWholeContacts();
	}

	private void onEventOfInterestReportMessage(EventOfInterestReportMessage msg) {
		System.out.println("Event of interest report received: affectedId " + msg.getAffectedId() + ".");
		sender().tell(new EventOfInterestAckMessage(msg.getAffectedId()), self());
	}

	private void addDirectionalContact(int deviceId, int otherDeviceId, long timestamp) {
		// Get container of contacts of "deviceId" (if it is absent it is created).
		ContactsOfSingleDevice contactsOfDevice = contacts.computeIfAbsent(deviceId, ContactsOfSingleDevice::new);

		// Add contact "deviceId"->"otherDeviceId" using the container of contacts of "deviceId".
		contactsOfDevice.addOrUpdateContact(otherDeviceId, timestamp);
	}

	private void debugPrintWholeContacts() {
		System.out.println("######### DEBUG CURRENT CONTACTS #########");
		for(Map.Entry<Integer, ContactsOfSingleDevice> entry : contacts.entrySet()) {
			ContactsOfSingleDevice contactsOfSingleDevice = entry.getValue();
			System.out.println(contactsOfSingleDevice.toString());
		}
		System.out.println("##########################################");
	}
	//endregion
}
