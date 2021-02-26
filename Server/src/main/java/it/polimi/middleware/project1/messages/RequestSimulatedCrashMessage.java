package it.polimi.middleware.project1.messages;

import java.io.Serializable;

/**
 * Message representing a request to simulate a crash in a server of a certain region characterized by the name <code>region</code>.
 */
public class RequestSimulatedCrashMessage implements Serializable {

	public final String region;

	public RequestSimulatedCrashMessage(String region) {
		this.region = region;
	}
}
