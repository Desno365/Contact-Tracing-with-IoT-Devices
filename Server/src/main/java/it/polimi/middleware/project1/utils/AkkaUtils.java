package it.polimi.middleware.project1.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AkkaUtils {

	public static final String ACTOR_SYSTEM_NAME = "contact-tracing-system";

	private static final String AKKA_CONFIG = "akka.conf";

	private AkkaUtils() {
		throw new IllegalStateException("Utils class with static methods. Should not be instantiated.");
	}

	public static String getEventOfInterestTopicForRegion(String region) {
		return "event-of-interest-" + region;
	}

	public static Config getAkkaConfigWithCustomPort(int port) {
		final Config myConfig = ConfigFactory.parseString("akka.remote.artery.canonical.port=" + port);
		final Config regularConfig = ConfigFactory.parseResources(AkkaUtils.AKKA_CONFIG);
		final Config combinedConfig = myConfig.withFallback(regularConfig);
		return ConfigFactory.load(combinedConfig);
	}

}
