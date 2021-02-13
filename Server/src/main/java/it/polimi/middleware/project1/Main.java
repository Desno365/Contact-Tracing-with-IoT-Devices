package it.polimi.middleware.project1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.*;

public class Main {

	private static HashMap<Integer, HashSet<Integer>> contacts = new HashMap<>();

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
			System.out.println("Error: " + err.toString());
		}
	}

	private static synchronized void processContactInformation(int deviceId1, int deviceId2) {
		// Get sets of contacts for each device.
		HashSet<Integer> set1 = contacts.get(deviceId1);
		HashSet<Integer> set2 = contacts.get(deviceId2);

		// If sets of contacts are null, create them.
		if(set1 == null) {
			set1 = new HashSet<>();
			contacts.put(deviceId1, set1);
		}
		if(set2 == null) {
			set2 = new HashSet<>();
			contacts.put(deviceId2, set2);
		}

		// Add single contact to the sets of the two devices.
		set1.add(deviceId2);
		set2.add(deviceId1);

		debugPrintWholeContacts();
	}

	private static void debugPrintWholeContacts() {
		System.out.println("######### DEBUG CURRENT CONTACTS #########");
		for(Map.Entry<Integer, HashSet<Integer>> entry : contacts.entrySet()) {
			Integer deviceId = entry.getKey();
			HashSet<Integer> contactsForDevice = entry.getValue();
			System.out.println("Contacts of device " + deviceId + ": " + contactsForDevice.toString());
		}
		System.out.println("##########################################");
	}
}
