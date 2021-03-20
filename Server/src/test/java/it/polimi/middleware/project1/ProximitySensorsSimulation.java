package it.polimi.middleware.project1;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Emulates the IOT proximity sensors.
 */
public class ProximitySensorsSimulation {

	public static final int NUMBER_OF_SENSORS = 10;

	private static final int INITIAL_DELAY_IN_SECONDS = 5;
	private static final int PERIOD_IN_SECONDS = 5;
	private static final int STOP_AFTER_SECONDS = 7;

	private static final HashMap<String, ProximitySensor> proximitySensorHashMap = new HashMap<>();

	public static void main(String[] args) {
		// Create proximity sensors.
		for(int i = 1; i <= NUMBER_OF_SENSORS; i++) {
			final int regionNumber = (i % 3) + 1;
			final String region =  "region" + regionNumber;
			final String deviceId = getDeviceIdFromDeviceNumber(i);
			final ProximitySensor proximitySensor = new ProximitySensor(deviceId, region);
			proximitySensorHashMap.put(deviceId, proximitySensor);
		}

		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
		final ScheduledFuture<?> schedule1 = scheduler.scheduleAtFixedRate(ProximitySensorsSimulation::createSimulatedContact, INITIAL_DELAY_IN_SECONDS, PERIOD_IN_SECONDS, TimeUnit.SECONDS);
		final ScheduledFuture<?> schedule2 = scheduler.scheduleAtFixedRate(ProximitySensorsSimulation::createSimulatedContact, INITIAL_DELAY_IN_SECONDS, PERIOD_IN_SECONDS, TimeUnit.SECONDS);
		final ScheduledFuture<?> schedule3 = scheduler.scheduleAtFixedRate(ProximitySensorsSimulation::createSimulatedContact, INITIAL_DELAY_IN_SECONDS, PERIOD_IN_SECONDS, TimeUnit.SECONDS);

		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				schedule1.cancel(false);
				schedule2.cancel(false);
				schedule3.cancel(false);
			}
		}, STOP_AFTER_SECONDS * 1000);
	}

	private static void createSimulatedContact() {
		// Get a random deviceId.
		final String deviceId = getRandomValidDeviceId();

		// Get another deviceId, but make sure that is different from the previous one.
		String otherDeviceId = deviceId;
		while(otherDeviceId.equals(deviceId))
			otherDeviceId = ProximitySensorsSimulation.getRandomValidDeviceId();

		// Send contact message.
		proximitySensorHashMap.get(deviceId).sendSimulatedContactMessage(otherDeviceId);
		proximitySensorHashMap.get(otherDeviceId).sendSimulatedContactMessage(deviceId);
	}

	public static String getRandomValidDeviceId() {
		final int deviceNumber = new Random().nextInt(ProximitySensorsSimulation.NUMBER_OF_SENSORS) + 1;
		return getDeviceIdFromDeviceNumber(deviceNumber);
	}

	public static String getDeviceIdFromDeviceNumber(int deviceNumber) {
		return "fe80::" + deviceNumber;
	}

}
