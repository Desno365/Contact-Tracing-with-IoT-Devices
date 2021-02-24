package it.polimi.middleware.project1;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ProximitySensorsSimulation {

	public static final int NUMBER_OF_SENSORS = 10;

	private static final int INITIAL_DELAY_IN_SECONDS = 5;
	private static final int PERIOD_IN_SECONDS = 5;
	private static final int STOP_AFTER_SECONDS = 17;

	private static final HashMap<Integer, ProximitySensor> proximitySensorHashMap = new HashMap<>();

	public static void main(String[] args) {
		// Create proximity sensors.
		for(int deviceId = 1; deviceId <= NUMBER_OF_SENSORS; deviceId++) {
			final String region = getExampleRegionForDeviceId(deviceId);
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

	private static String getExampleRegionForDeviceId(int deviceId) {
		int regionNumber = (deviceId % 3) + 1;
		return "region" + regionNumber;
	}

	private static void createSimulatedContact() {
		// Get a random deviceId.
		final int deviceId = getRandomValidDeviceId();

		// Get another deviceId, but make sure that is different from the previous one.
		int otherDeviceId = deviceId;
		while(otherDeviceId == deviceId)
			otherDeviceId = ProximitySensorsSimulation.getRandomValidDeviceId();

		// Send contact message.
		proximitySensorHashMap.get(deviceId).sendSimulatedContactMessage(otherDeviceId);
		proximitySensorHashMap.get(otherDeviceId).sendSimulatedContactMessage(deviceId);
	}

	public static int getRandomValidDeviceId() {
		return new Random().nextInt(ProximitySensorsSimulation.NUMBER_OF_SENSORS) + 1;
	}

}
