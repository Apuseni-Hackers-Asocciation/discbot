/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package eu.apuseni.discbot;

import java.io.FileNotFoundException;
import java.io.IOException;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

public class App {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String discordToken = System.getenv("DISCORD_TOKEN");
		GatewayDiscordClient client = DiscordClientBuilder.create(discordToken).build().login().block();

		EventDispatcher eventDispatcher = client.getEventDispatcher();
		eventDispatcher.on(ReadyEvent.class).subscribe(event -> {
			final User self = event.getSelf();
			System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
		});

		Help helpCmd = new Help();
		eventDispatcher.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(msg -> helpCmd.test(msg.getContent())).subscribe(helpCmd::execute, App::errorHandler);

		Version versionCmd = new Version();
		helpCmd.register(versionCmd);
		eventDispatcher.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(msg -> versionCmd.test(msg.getContent())).subscribe(versionCmd::execute, App::errorHandler);

//		eventDispatcher.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
//		.filter(msg -> versionCmd.test(msg.getContent())).flatMap(null).onE

		NewContest newContest = new NewContest();
		helpCmd.register(newContest);
		eventDispatcher.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(msg -> newContest.test(msg.getContent())).subscribe(newContest::execute, App::errorHandler);

//		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage).subscribe(msg -> {
//			System.out.println("message");
//			long channel = msg.getChannelId().asLong();
//			String username = msg.getAuthor().get().getUsername();
//			msg.getId().asLong();
//			long timeStamp = msg.getTimestamp().toEpochMilli();
//			System.out.printf("%d: %s@%d: %s\n", timeStamp, username, channel, msg.getContent());
//		});

		client.onDisconnect().block();

	}

	private static void errorHandler(Throwable th) {
		th.printStackTrace();
	}
}
