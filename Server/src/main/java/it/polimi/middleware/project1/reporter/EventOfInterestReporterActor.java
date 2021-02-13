package it.polimi.middleware.project1.reporter;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.dispatch.OnComplete;
import akka.util.Timeout;
import it.polimi.middleware.project1.messages.EventOfInterestAckMessage;
import it.polimi.middleware.project1.messages.EventOfInterestReportMessage;
import scala.concurrent.ExecutionContext;

import java.util.concurrent.TimeUnit;

public class EventOfInterestReporterActor extends AbstractActor {

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(EventOfInterestReportMessage.class, this::onEventOfInterestReportMessage)
				.match(EventOfInterestAckMessage.class, this::onEventOfInterestAckMessage)
				.build();
	}

	private void onEventOfInterestReportMessage(EventOfInterestReportMessage msg) {
		System.out.println("Sending event of interest. Affected device id: " + msg.getAffectedId() + ".");

		ActorSelection selection = getContext().getSystem().actorSelection("akka://contact-tracing-system@127.0.0.1:6123/user/ServerActor");
		final ExecutionContext ec = getContext().getSystem().dispatcher();
		selection.resolveOne(new Timeout(10, TimeUnit.SECONDS)).andThen(new OnComplete<ActorRef>() {
			public void onComplete(Throwable failure, ActorRef actorRef) {
				if(actorRef != null)
					actorRef.tell(msg, self());
				else
					System.out.println("ServerActor not found!");
			}
		}, ec);
	}

	private void onEventOfInterestAckMessage(EventOfInterestAckMessage msg) {
		System.out.println("Received ack of event of interest. Affected device id: " + msg.getAffectedId() + ".");
	}

}
