package it.polimi.middleware.project1.messages;

import java.io.Serializable;

public class EventOfInterestAckMessage implements Serializable {

	public final int affectedId;
	public final String region;

	public EventOfInterestAckMessage(int affectedId, String region) {
		this.affectedId = affectedId;
		this.region = region;
	}
}
