package it.polimi.middleware.project1.server;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttUtils {

	public static final String DEFAULT_BROKER = "tcp://mqtt.neslab.it:3200";
	public static final int DEFAULT_QOS = 1;
	public static final String CONTACT_TOPIC = "AccordiBurattiMotta-Contact-Tracing/contact/json";

	private MqttUtils() {
		throw new IllegalStateException("Utils class with static methods. Should not be instantiated.");
	}

	public static void logMqttException(MqttException me) {
		System.out.println("reason: " + me.getReasonCode());
		System.out.println("msg: " + me.getMessage());
		System.out.println("loc: " + me.getLocalizedMessage());
		System.out.println("cause: " + me.getCause());
		System.out.println("excep: " + me);
		me.printStackTrace();
	}
}
