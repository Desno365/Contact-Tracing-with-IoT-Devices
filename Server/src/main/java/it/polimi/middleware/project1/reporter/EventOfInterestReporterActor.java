package it.polimi.middleware.project1.reporter;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import it.polimi.middleware.project1.messages.EventOfInterestAckMessage;
import it.polimi.middleware.project1.messages.EventOfInterestReportMessage;
import it.polimi.middleware.project1.messages.RequestSimulatedCrashMessage;
import it.polimi.middleware.project1.utils.AkkaUtils;

/**
 * This class represents an event of interest reporter.
 * An event of interest reporter is able to communicate the happening of an event of interest to a server of a certain region.
 */
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
				.match(RequestSimulatedCrashMessage.class, this::onRequestSimulatedCrashMessage)
				.build();
	}


	// ##############################################################
	//region Private methods
	// ###############################

	private void onEventOfInterestReportMessage(EventOfInterestReportMessage msg) {
		System.out.println("Sending event of interest. Affected device id: " + msg.affectedId + ".");

		// Send event of interest to region.
		final String topic = AkkaUtils.getEventOfInterestTopicForRegion(msg.region);
		mediatorActorRef.tell(new DistributedPubSubMediator.Publish(topic, msg), getSelf());
	}

	private void onEventOfInterestAckMessage(EventOfInterestAckMessage msg) {
		System.out.println("Received ack of event of interest. Affected device id: " + msg.affectedId + ".");
	}

	private void onRequestSimulatedCrashMessage(RequestSimulatedCrashMessage msg) {
		System.out.println("Sending request of simulated crash. Region: " + msg.region + ".");

		// Send request of simulated crash to region.
		final String topic = AkkaUtils.getEventOfInterestTopicForRegion(msg.region);
		mediatorActorRef.tell(new DistributedPubSubMediator.Publish(topic, msg), getSelf());
	}
	//endregion

}
