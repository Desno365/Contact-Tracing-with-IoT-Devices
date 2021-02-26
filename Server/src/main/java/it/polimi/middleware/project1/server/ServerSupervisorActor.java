package it.polimi.middleware.project1.server;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;

import java.time.Duration;

/**
 * This class represents the server supervisor actor.
 * This supervisor is able to manage exceptions of the server actor and restart the server if needed.
 */
public class ServerSupervisorActor extends AbstractActor {

	// #strategy
	private static final SupervisorStrategy strategy =
			new OneForOneStrategy(
					10,
					Duration.ofMinutes(1),
					DeciderBuilder
							.match(Exception.class, e -> (SupervisorStrategy.Directive) SupervisorStrategy.restart())
							.build()
			);

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Props.class, props -> getSender().tell(getContext().actorOf(props), getSelf()))
				.build();
	}
}
