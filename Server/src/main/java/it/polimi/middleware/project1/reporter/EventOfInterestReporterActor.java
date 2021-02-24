package it.polimi.middleware.project1.reporter;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import it.polimi.middleware.project1.messages.EventOfInterestAckMessage;
import it.polimi.middleware.project1.messages.EventOfInterestReportMessage;
import it.polimi.middleware.project1.utils.AkkaUtils;

public class EventOfInterestReporterActor extends AbstractActor {

	private final ActorRef mediatorActorRef;

	public EventOfInterestReporterActor() {
		this.mediatorActorRef = DistributedPubSub.get(getContext().getSystem()).mediator();
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(EventOfInterestReportMessage.class, this::onEventOfInterestReportMessage)
				.match(EventOfInterestAckMessage.class, this::onEventOfInterestAckMessage)
				.build();
	}

	private void onEventOfInterestReportMessage(EventOfInterestReportMessage msg) {
		System.out.println("Sending event of interest. Affected device id: " + msg.affectedId + ".");

		final String topic = AkkaUtils.getEventOfInterestTopicForRegion(msg.region);
		mediatorActorRef.tell(new DistributedPubSubMediator.Publish(topic, msg), getSelf());
	}

	private void onEventOfInterestAckMessage(EventOfInterestAckMessage msg) {
		System.out.println("Received ack of event of interest. Affected device id: " + msg.affectedId + ".");
	}

}
