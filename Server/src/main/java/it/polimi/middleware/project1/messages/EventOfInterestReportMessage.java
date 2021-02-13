package it.polimi.middleware.project1.messages;

import java.io.Serializable;

public class EventOfInterestReportMessage implements Serializable {

	private final int affectedId;

	public int getAffectedId() {
		return affectedId;
	}

	public EventOfInterestReportMessage(int affectedId) {
		this.affectedId = affectedId;
	}
}
