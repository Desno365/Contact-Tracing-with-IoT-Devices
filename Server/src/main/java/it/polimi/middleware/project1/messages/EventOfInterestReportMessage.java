package it.polimi.middleware.project1.messages;

import akka.actor.ActorRef;

import java.io.Serializable;

/**
 * Message representing an event of interest involving device with id <code>affectedId</code>.
 * The device with id <code>affectedId</code> is managed by the region characterized by the name <code>region</code>.
 * The message also uses the Request-Response pattern: in fact it contains reference of the actor that created the event of interest,
 * so that it is possible to send back an ack message.
 */
public class EventOfInterestReportMessage implements Serializable {

	public final String affectedId;
	public final String region;
	public final ActorRef sendAckTo;

	public EventOfInterestReportMessage(String affectedId, String region, ActorRef sendAckTo) {
		this.affectedId = affectedId;
		this.region = region;
		this.sendAckTo = sendAckTo;
	}
}
