package it.polimi.middleware.project1.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import it.polimi.middleware.project1.utils.AkkaUtils;
import it.polimi.middleware.project1.utils.MqttUtils;

import java.util.concurrent.TimeoutException;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Main {

	private static final int DEFAULT_TIMEOUT_IN_MILLISECONDS = 10000;
	private static final scala.concurrent.duration.Duration DEFAULT_TIMEOUT_DURATION = scala.concurrent.duration.Duration.create(DEFAULT_TIMEOUT_IN_MILLISECONDS, MILLISECONDS);

	public static void main(String[] args) {
		final String region = args.length > 0 ? args[0] : "region1";
		final int port = args.length > 1 ? Integer.parseInt(args[1]) : 6123;

		final Config config = AkkaUtils.getAkkaConfigWithCustomPort(port);
		final ActorSystem sys = ActorSystem.create(AkkaUtils.ACTOR_SYSTEM_NAME, config);
		final ActorRef supervisorActorRef = sys.actorOf(Props.create(ServerSupervisorActor.class), "ServerSupervisorActor-" + region);
		scala.concurrent.Future<Object> waitingForServer = ask(supervisorActorRef, Props.create(ServerActor.class, MqttUtils.DEFAULT_BROKER, region), DEFAULT_TIMEOUT_IN_MILLISECONDS);
		try {
			final ActorRef serverActorRef = (ActorRef) waitingForServer.result(DEFAULT_TIMEOUT_DURATION, null);
		} catch (InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}
	}
}
