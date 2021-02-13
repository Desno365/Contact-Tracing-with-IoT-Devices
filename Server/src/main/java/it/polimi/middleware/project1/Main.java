package it.polimi.middleware.project1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.*;

public class Main {

	private static final HashMap<Integer, ContactsOfSingleDevice> contacts = new HashMap<>();

	public static void main(String[] args) {
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			String subscriberId = UUID.randomUUID().toString();
			MqttClient subscriber = new MqttClient(MqttUtils.BROKER, subscriberId, persistence);

			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);

			System.out.println("Connecting to broker: " + MqttUtils.BROKER);
			subscriber.connect(options);
			System.out.println("Connected");

			System.out.println("Subscribing to messages.");
			subscriber.subscribe(MqttUtils.CONTACT_TOPIC, MqttUtils.QOS, Main::processContactMessage);
			System.out.println("Subscribed.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	private static void processContactMessage(String topic, MqttMessage mqttMessage) {
		final String stringPayload = new String(mqttMessage.getPayload());
		System.out.println("Message received: topic \"" + topic + "\"; payload: \"" + stringPayload + "\".");

		try {
			final JSONObject jsonObject = new JSONObject(stringPayload);
			final int myId = jsonObject.getJSONObject("contact").getInt("myId");
			final int otherId = jsonObject.getJSONObject("contact").getInt("otherId");
			System.out.println("Information received: myId " + myId + ", otherId: " + otherId + ".");
			processContactInformation(myId, otherId);
		} catch(Exception err) {
			System.out.println("Failed to process json, error: " + err.toString());
		}
	}

	private static void processContactInformation(int deviceId1, int deviceId2) {
		if(deviceId1 == deviceId2) {
			System.out.println("Device in contact with itself, ignored.");
			return;
		}

		// Two directional contacts since the contact is bidirectional.
		final long timestamp = System.currentTimeMillis();
		addDirectionalContact(deviceId1, deviceId2, timestamp);
		addDirectionalContact(deviceId2, deviceId1, timestamp);

		debugPrintWholeContacts();
	}

	private static void addDirectionalContact(int deviceId, int otherDeviceId, long timestamp) {
		// Get container of contacts of "deviceId" (if it is absent it is created).
		ContactsOfSingleDevice contactsOfDevice = contacts.computeIfAbsent(deviceId, ContactsOfSingleDevice::new);

		// Add contact "deviceId"->"otherDeviceId" using the container of contacts of "deviceId".
		contactsOfDevice.addOrUpdateContact(otherDeviceId, timestamp);
	}

	private static void debugPrintWholeContacts() {
		System.out.println("######### DEBUG CURRENT CONTACTS #########");
		for(Map.Entry<Integer, ContactsOfSingleDevice> entry : contacts.entrySet()) {
			ContactsOfSingleDevice contactsOfSingleDevice = entry.getValue();
			System.out.println(contactsOfSingleDevice.toString());
		}
		System.out.println("##########################################");
	}
}
