package it.polimi.middleware.project1.reporter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import it.polimi.middleware.project1.messages.EventOfInterestReportMessage;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		final Config conf = ConfigFactory.parseResources("reporter.conf");
		final ActorSystem sys = ActorSystem.create("contact-tracing-system", conf);
		ActorRef eventOfInterestReporterActorRef = sys.actorOf(Props.create(EventOfInterestReporterActor.class));

		Scanner scanner = new Scanner(System.in);
		int affectedId = 0;
		while(affectedId != -1) {
			System.out.println("Enter device id affected by event of interest (-1 to exit):");
			affectedId = scanner.nextInt();
			if(affectedId != -1)
				eventOfInterestReporterActorRef.tell(new EventOfInterestReportMessage(affectedId), ActorRef.noSender());
		}

		sys.terminate();
	}
}
