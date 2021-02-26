package it.polimi.middleware.project1.reporter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import it.polimi.middleware.project1.messages.EventOfInterestReportMessage;
import it.polimi.middleware.project1.messages.RequestSimulatedCrashMessage;
import it.polimi.middleware.project1.utils.AkkaUtils;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		final int port = args.length > 0 ? Integer.parseInt(args[0]) : 6223;

		final Config config = AkkaUtils.getAkkaConfigWithCustomPort(port);
		final ActorSystem sys = ActorSystem.create(AkkaUtils.ACTOR_SYSTEM_NAME, config);
		ActorRef eventOfInterestReporterActorRef = sys.actorOf(Props.create(EventOfInterestReporterActor.class));

		String region = "";
		while(isNotExitCommand(region)) {
			region = insertRegion();
			if(isNotExitCommand(region)) {
				int affectedId = insertDeviceId();
				if(affectedId >= 0) {
					eventOfInterestReporterActorRef.tell(new EventOfInterestReportMessage(affectedId, region, eventOfInterestReporterActorRef), ActorRef.noSender());
					Thread.sleep(2000);
				} else {
					if(affectedId == -1) {
						eventOfInterestReporterActorRef.tell(new RequestSimulatedCrashMessage(region), ActorRef.noSender());
						Thread.sleep(2000);
					} else {
						System.out.println("Device id should be a positive number!");
					}
				}
			}
		}

		sys.terminate();
	}

	private static boolean isNotExitCommand(String command) {
		command = command.toLowerCase();
		boolean isExitCommand = command.equals("exit") || command.equals("quit");
		return !isExitCommand;
	}

	private static String insertRegion() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("##############################################");
		System.out.println("Enter region or type exit:");
		System.out.println("##############################################");
		return scanner.next().toLowerCase();
	}

	private static int insertDeviceId() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("##############################################");
		System.out.println("Enter device id affected by event of interest (-1 to simulate crash):");
		System.out.println("##############################################");
		try {
			return scanner.nextInt();
		} catch(Exception e) {
			return -1;
		}
	}

}
