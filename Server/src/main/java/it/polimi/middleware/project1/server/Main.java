package it.polimi.middleware.project1.server;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import it.polimi.middleware.project1.utils.AkkaUtils;
import it.polimi.middleware.project1.utils.MqttUtils;

public class Main {

	public static void main(String[] args) {
		final String region = args.length > 0 ? args[0] : "region1";
		final int port = args.length > 1 ? Integer.parseInt(args[1]) : 6123;

		final Config config = AkkaUtils.getAkkaConfigWithCustomPort(port);
		final ActorSystem sys = ActorSystem.create(AkkaUtils.ACTOR_SYSTEM_NAME, config);
		sys.actorOf(Props.create(ServerActor.class, MqttUtils.DEFAULT_BROKER, region), "ServerActor-" + region);
	}
}
