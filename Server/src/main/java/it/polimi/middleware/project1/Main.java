package it.polimi.middleware.project1;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public class Main {

	private static final String BROKER = "tcp://mqtt.neslab.it:3200";
	private static final int QOS = 1;
	private static final String CONTACT_TOPIC = "AccordiBurattiMotta-Contact-Tracing/contact/json";

	public static void main(String[] args) {
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			String subscriberId = UUID.randomUUID().toString();
			MqttClient subscriber = new MqttClient(BROKER, subscriberId, persistence);

			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);

			System.out.println("Connecting to broker: " + BROKER);
			subscriber.connect(options);
			System.out.println("Connected");

			System.out.println("Subscribing to messages.");
			subscriber.subscribe(CONTACT_TOPIC, QOS, (topic, msg) -> {
				byte[] payload = msg.getPayload();
				String stringPayload = new String(payload);
				System.out.println("Message received: topic \"" + topic + "\"; payload: \"" + stringPayload + "\".");
			});
			System.out.println("Subscribed.");
		} catch(MqttException me) {
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();
		}
	}
}
