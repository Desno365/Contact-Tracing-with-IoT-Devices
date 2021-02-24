package it.polimi.middleware.project1;

import it.polimi.middleware.project1.utils.MqttUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.Date;

public class ProximitySensor {

	private final int deviceId;
	private final String subscriberId;
	private final String producerId;

	private IMqttClient mqttProducer;

	public ProximitySensor(int deviceId) {
		this.deviceId = deviceId;
		this.subscriberId = "sensor" + deviceId + "-sub";
		this.producerId = "sensor" + deviceId + "-prod";

		try {
			final MemoryPersistence subscriberPersistence = new MemoryPersistence();
			final MqttClient mqttSubscriber = new MqttClient(MqttUtils.DEFAULT_BROKER, subscriberId, subscriberPersistence);
			final MemoryPersistence producerPersistence = new MemoryPersistence();
			mqttProducer = new MqttClient(MqttUtils.DEFAULT_BROKER, producerId, producerPersistence);

			final MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);

			System.out.println(deviceId + ": connecting to broker: " + MqttUtils.DEFAULT_BROKER + ".");
			mqttSubscriber.connect(options);
			mqttProducer.connect(options);
			System.out.println(deviceId + ": connected.");

			final String notificationTopic = MqttUtils.getNotificationTopicForDevice(deviceId);
			System.out.println(deviceId + ": subscribing to messages of topic \"" + notificationTopic + "\".");
			mqttSubscriber.subscribe(notificationTopic, MqttUtils.DEFAULT_QOS, (String topic, MqttMessage mqttMessage) -> {
				final String stringPayload = new String(mqttMessage.getPayload());
				System.out.println(deviceId + ": MQTT message received: topic \"" + topic + "\"; payload: \"" + stringPayload + "\".");

				try {
					final JSONObject jsonObject = new JSONObject(stringPayload);
					final int receivedDeviceId = jsonObject.getJSONObject("notification").getInt("deviceId");
					final long timestampOfContact = jsonObject.getJSONObject("notification").getLong("timestampOfContact");
					final Date date = new Date(timestampOfContact);
					System.out.println(deviceId + ": Received notification of contact: device id received: " + receivedDeviceId + "; time of contact: " + date.toString() + ".");
				} catch(Exception err) {
					System.out.println("Failed to process json, error: " + err.toString());
				}
			});
			System.out.println(deviceId + ": subscribed.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	public void sendSimulatedContactMessage(int otherDeviceId) {
		assert deviceId != otherDeviceId;

		if(mqttProducer == null || !mqttProducer.isConnected()) {
			System.out.println(deviceId + ": not connected.");
			return;
		}

		final String payload = getContactMessage(otherDeviceId);
		try {
			System.out.println(deviceId + ": publishing message: " + payload + ".");
			MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
			mqttMessage.setQos(MqttUtils.DEFAULT_QOS);
			mqttProducer.publish(MqttUtils.getContactTopicForRegion("region1"), mqttMessage);
			System.out.println(deviceId + ": message published.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	private String getContactMessage(int otherDeviceId) {
		return "{\"contact\":{\"myId\":\"" + deviceId + "\",\"otherId\":\"" + otherDeviceId + "\"}}";
	}
}