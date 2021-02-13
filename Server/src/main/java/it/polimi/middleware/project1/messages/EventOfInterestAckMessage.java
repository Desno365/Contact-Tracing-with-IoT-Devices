package it.polimi.middleware.project1.messages;

import java.io.Serializable;

public class EventOfInterestAckMessage implements Serializable {

	private final int affectedId;

	public int getAffectedId() {
		return affectedId;
	}

	public EventOfInterestAckMessage(int affectedId) {
		this.affectedId = affectedId;
	}
}
