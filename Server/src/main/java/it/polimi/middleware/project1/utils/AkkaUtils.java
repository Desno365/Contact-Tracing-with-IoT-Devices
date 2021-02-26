package it.polimi.middleware.project1.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * This class contains utils methods and constants about the Akka structure of the architecture.
 */
public class AkkaUtils {

	public static final String ACTOR_SYSTEM_NAME = "contact-tracing-system";

	private static final String AKKA_CONFIG = "akka.conf";

	private AkkaUtils() {
		throw new IllegalStateException("Utils class with static methods. Should not be instantiated.");
	}

	/**
	 * Returns the name of the topic for an event of interest to be sent to a certain region.
	 * @param region the region that manages the specific event of interest.
	 * @return the name of the topic for an event of interest to be sent to a certain region.
	 */
	public static String getEventOfInterestTopicForRegion(String region) {
		return "event-of-interest-" + region;
	}

	/**
	 * Returns the Akka config but with a custom port.
	 * @param port the custom port to use in the Akka config.
	 * @return the config with a custom port.
	 */
	public static Config getAkkaConfigWithCustomPort(int port) {
		final Config myConfig = ConfigFactory.parseString("akka.remote.artery.canonical.port=" + port);
		final Config regularConfig = ConfigFactory.parseResources(AkkaUtils.AKKA_CONFIG);
		final Config combinedConfig = myConfig.withFallback(regularConfig);
		return ConfigFactory.load(combinedConfig);
	}

}
