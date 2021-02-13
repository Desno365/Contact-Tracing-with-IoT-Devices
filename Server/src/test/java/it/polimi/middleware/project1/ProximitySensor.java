package it.polimi.middleware.project1;

import it.polimi.middleware.project1.server.MqttUtils;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ProximitySensor {

	private final int deviceId;
	private final String producerId;

	private IMqttClient producer;

	public ProximitySensor(int deviceId) {
		this.deviceId = deviceId;
		this.producerId = "proximity-sensor" + deviceId;
		try {
			final MemoryPersistence persistence = new MemoryPersistence();
			producer = new MqttClient(MqttUtils.DEFAULT_BROKER, producerId, persistence);

			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);

			System.out.println(producerId + ": connecting to broker: " + MqttUtils.DEFAULT_BROKER + ".");
			producer.connect(options);
			System.out.println(producerId + ": connected.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	public void sendSimulatedContactMessage() {
		if(producer == null || !producer.isConnected()) {
			System.out.println(producerId + ": not connected.");
			return;
		}

		int otherDeviceId = deviceId;
		while(otherDeviceId == deviceId)
			otherDeviceId = ProximitySensorsSimulation.getRandomValidDeviceId();

		final String payload = getContactMessage(otherDeviceId);

		try {
			System.out.println(producerId + ": publishing message: " + payload + ".");
			MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
			mqttMessage.setQos(MqttUtils.DEFAULT_QOS);
			producer.publish(MqttUtils.CONTACT_TOPIC, mqttMessage);
			System.out.println(producerId + ": message published.");
		} catch(MqttException me) {
			MqttUtils.logMqttException(me);
		}
	}

	private String getContactMessage(int otherDeviceId) {
		return "{\"contact\":{\"myId\":\"" + deviceId + "\",\"otherId\":\"" + otherDeviceId + "\"}}";
	}
}