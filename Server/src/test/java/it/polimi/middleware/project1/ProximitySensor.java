package it.polimi.middleware.project1;

import it.polimi.middleware.project1.utils.MqttUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.Date;

public class ProximitySensor {

	private final String deviceId;
	private final String region;

	private IMqttClient mqttProducer;

	public ProximitySensor(String deviceId, String region) {
		this.deviceId = deviceId;
		this.region = region;
		String subscriberId = "sensor" + deviceId + "-sub";
		String producerId = "sensor" + deviceId + "-prod";
		log("Initializing sensor with predefined region: " + region + ".");

		try {
			final MemoryPersistence subscriberPersistence = new MemoryPersistence();
			final MqttClient mqttSubscriber = new MqttClient(MqttUtils.DEFAULT_BROKER, subscriberId, subscriberPersistence);
			final MemoryPersistence producerPersistence = new MemoryPersistence();
			mqttProducer = new MqttClient(MqttUtils.DEFAULT_BROKER, producerId, producerPersistence);

			final MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);

			log("Connecting to broker: " + MqttUtils.DEFAULT_BROKER + ".");
			mqttSubscriber.connect(options);
			mqttProducer.connect(options);
			log("Connected.");

			final String notificationTopic = MqttUtils.getNotificationTopicForDevice(deviceId);
			log("Subscribing to messages of topic \"" + notificationTopic + "\".");
			mqttSubscriber.subscribe(notificationTopic, MqttUtils.DEFAULT_QOS, (String topic, MqttMessage mqttMessage) -> {
				final String stringPayload = new String(mqttMessage.getPayload());
				log("MQTT message received: topic \"" + topic + "\"; payload: \"" + stringPayload + "\".");

				try {
					final JSONObject jsonObject = new JSONObject(stringPayload);
					final String receivedDeviceId = jsonObject.getJSONObject("notification").getString("deviceId");
					final long timestampOfContact = jsonObject.getJSONObject("notification").getLong("timestampOfContact");
					final Date date = new Date(timestampOfContact);
					log("Received notification of contact: device id received: " + receivedDeviceId + "; time of contact: " + date.toString() + ".");
				} catch(Exception err) {
					log("Failed to process json, error: " + err.toString());
				}
			});
			log("Subscribed.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	public void sendSimulatedContactMessage(String otherDeviceId) {
		assert !deviceId.equals(otherDeviceId);

		if(mqttProducer == null || !mqttProducer.isConnected()) {
			log("Not connected.");
			return;
		}

		final String payload = getContactMessage(otherDeviceId);
		try {
			log("Publishing message: " + payload + ".");
			MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
			mqttMessage.setQos(MqttUtils.DEFAULT_QOS);
			mqttProducer.publish(MqttUtils.getContactTopicForRegion(region), mqttMessage);
			System.out.println(deviceId + ": message published.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	private String getContactMessage(String otherDeviceId) {
		return "{\"contact\":{\"myId\":\"" + deviceId + "\",\"otherIds\":[\"" + otherDeviceId + "\"]}}";
	}

	private void log(String message) {
		System.out.println(deviceId + ": " + message);
	}
}