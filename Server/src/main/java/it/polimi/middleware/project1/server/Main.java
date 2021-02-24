package it.polimi.middleware.project1.server;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import it.polimi.middleware.project1.utils.AkkaUtils;
import it.polimi.middleware.project1.utils.MqttUtils;

public class Main {

	public static void main(String[] args) {
		final Config conf = ConfigFactory.parseResources(AkkaUtils.SERVER_CONFIG);
		final ActorSystem sys = ActorSystem.create(AkkaUtils.ACTOR_SYSTEM_NAME, conf);
		sys.actorOf(Props.create(ServerActor.class, MqttUtils.DEFAULT_BROKER, "region1"), "ServerActor-region1");
		sys.actorOf(Props.create(ServerActor.class, MqttUtils.DEFAULT_BROKER, "region2"), "ServerActor-region2");
		sys.actorOf(Props.create(ServerActor.class, MqttUtils.DEFAULT_BROKER, "region3"), "ServerActor-region3");
	}
}
