package it.polimi.middleware.project1.utils;

public class AkkaUtils {

	public static final String SERVER_CONFIG = "server.conf";
	public static final String EVENT_OF_INTEREST_REPORTER_CONFIG = "reporter.conf";
	public static final String ACTOR_SYSTEM_NAME = "contact-tracing-system";

	private AkkaUtils() {
		throw new IllegalStateException("Utils class with static methods. Should not be instantiated.");
	}

	public static String getEventOfInterestTopicForRegion(String region) {
		return "event-of-interest-" + region;
	}

}
