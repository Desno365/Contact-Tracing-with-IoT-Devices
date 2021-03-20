package it.polimi.middleware.project1.messages;

import java.io.Serializable;

/**
 * Ack message for an event of interest involving device with id <code>affectedId</code>.
 * The device with id <code>affectedId</code> is managed by the region characterized by the name <code>region</code>.
 */
public class EventOfInterestAckMessage implements Serializable {

	public final int affectedId;
	public final String region;

	public EventOfInterestAckMessage(int affectedId, String region) {
		this.affectedId = affectedId;
		this.region = region;
	}
}
