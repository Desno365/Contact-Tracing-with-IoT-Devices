package it.polimi.middleware.project1;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProximitySensorsSimulation {

	public static final int NUMBER_OF_SENSORS = 10;

	private static final HashMap<Integer, ProximitySensor> proximitySensorHashMap = new HashMap<>();

	public static void main(String[] args) {
		// Create proximity sensors.
		for(int deviceId = 1; deviceId <= NUMBER_OF_SENSORS; deviceId++) {
			final ProximitySensor proximitySensor = new ProximitySensor(deviceId);
			proximitySensorHashMap.put(deviceId, proximitySensor);
		}

		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
		scheduler.scheduleAtFixedRate(ProximitySensorsSimulation::createSimulatedContact, 5000, 5000, TimeUnit.MILLISECONDS);
		scheduler.scheduleAtFixedRate(ProximitySensorsSimulation::createSimulatedContact, 4999, 5000, TimeUnit.MILLISECONDS);
		scheduler.scheduleAtFixedRate(ProximitySensorsSimulation::createSimulatedContact, 4998, 5000, TimeUnit.MILLISECONDS);
	}

	public static int getRandomValidDeviceId() {
		return new Random().nextInt(ProximitySensorsSimulation.NUMBER_OF_SENSORS) + 1;
	}

	private static void createSimulatedContact() {
		final int deviceId = getRandomValidDeviceId();
		proximitySensorHashMap.get(deviceId).sendSimulatedContactMessage();
	}

}
