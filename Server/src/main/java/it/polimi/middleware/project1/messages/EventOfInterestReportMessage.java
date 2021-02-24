package it.polimi.middleware.project1.messages;

import akka.actor.ActorRef;

import java.io.Serializable;

public class EventOfInterestReportMessage implements Serializable {

	public final int affectedId;
	public final String region;
	public final ActorRef sendAckTo;

	public EventOfInterestReportMessage(int affectedId, String region, ActorRef sendAckTo) {
		this.affectedId = affectedId;
		this.region = region;
		this.sendAckTo = sendAckTo;
	}
}
