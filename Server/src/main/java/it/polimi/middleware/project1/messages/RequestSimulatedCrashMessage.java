package it.polimi.middleware.project1.messages;

import java.io.Serializable;

public class RequestSimulatedCrashMessage implements Serializable {

	public final String region;

	public RequestSimulatedCrashMessage(String region) {
		this.region = region;
	}
}
