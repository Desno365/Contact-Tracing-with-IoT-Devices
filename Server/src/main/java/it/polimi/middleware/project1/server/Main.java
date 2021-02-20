package it.polimi.middleware.project1.server;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main {

	public static void main(String[] args) {
		final Config conf = ConfigFactory.parseResources("server.conf");
		final ActorSystem sys = ActorSystem.create("contact-tracing-system", conf);
		sys.actorOf(Props.create(ServerActor.class, MqttUtils.DEFAULT_BROKER, MqttUtils.getContactTopicForRegion("region1")), "ServerActor");
	}
}
