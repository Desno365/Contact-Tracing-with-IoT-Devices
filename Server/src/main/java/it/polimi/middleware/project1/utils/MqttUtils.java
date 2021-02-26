package it.polimi.middleware.project1.utils;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * This class contains utils methods and constants about the MQTT structure of the architecture.
 */
public class MqttUtils {

	public static final String DEFAULT_BROKER = "tcp://mqtt.neslab.it:3200";
	public static final int DEFAULT_QOS = 1;

	private MqttUtils() {
		throw new IllegalStateException("Utils class with static methods. Should not be instantiated.");
	}

	/**
	 * Logs a <code>MqttException</code>
	 * @param me the <code>MqttException</code>.
	 */
	public static void logMqttException(MqttException me) {
		System.out.println("reason: " + me.getReasonCode());
		System.out.println("msg: " + me.getMessage());
		System.out.println("loc: " + me.getLocalizedMessage());
		System.out.println("cause: " + me.getCause());
		System.out.println("excep: " + me);
		me.printStackTrace();
	}

	/**
	 * Returns the name of the topic for a contact to be sent to a certain region.
	 * @param region the region that should manage the contact.
	 * @return the name of the topic for a contact to be sent to a certain region.
	 */
	public static String getContactTopicForRegion(String region) {
		return "AccordiBurattiMotta-Topic/contact/" + region + "/json";
	}

	/**
	 * Returns the name of the topic for a notification to be sent to a certain device.
	 * @param deviceId the device that should receive the notification.
	 * @return the name of the topic for a notification to be sent to a certain device.
	 */
	public static String getNotificationTopicForDevice(int deviceId) {
		return "AccordiBurattiMotta-Topic/notif/sensor" + deviceId + "/json";
	}
}
