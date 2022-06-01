package fr.ensim.interop.introrest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.telegram.ApiResponseTelegram;
import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ListenerUpdateTelegram implements CommandLineRunner {

	@Value("${telegram.bot.id}")
	private String telegramBotId;
	@Value("${telegram.api.url}")
	private String telegramApiUrl;

	@Override
	public void run(String... args) throws Exception {
		Logger.getLogger("ListenerUpdateTelegram").log(Level.INFO, "Démarage du listener d'updates Telegram...");

		RestTemplate restTemplate = new RestTemplate();

		TimerTask task = new TimerTask() {
			int offset = -1;
			int updateNum = 0;

			public void run() {
				// Operation de pooling pour capter les evenements Telegram
				URI getUpdatesURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
						.path("/getUpdates")
						.queryParam("offset", offset)
						.build().encode().toUri();
				ApiResponseUpdateTelegram response = restTemplate.getForObject(getUpdatesURL,
						ApiResponseUpdateTelegram.class);
				System.out.println("----------- Get Updates num." + updateNum++);

				if (response != null && !response.getResult().isEmpty()) {
					String messageText = response.getResult().get(0).getMessage().getText();
					offset = response.getResult().get(0).getUpdateId();
					System.out.println("New offset = " + ++offset);
					System.out.println("messageText = " + messageText);
					// Traiter message reçu
					if (messageText.equalsIgnoreCase("joke")) {
						Joke jokeResponse = restTemplate.getForObject("http://127.0.0.1:9090/randomJoke",
								Joke.class);
						System.out.println(jokeResponse.getTitle());
						System.out.println(jokeResponse.getAnswer());

						// Envoi de la question
						URI messageURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
								.path("/message")
								.queryParam("chat_id", telegramBotId)
								.queryParam("text", jokeResponse.getTitle())
								.build().encode().toUri();
						restTemplate.getForObject(messageURL, ApiResponseTelegram.class);

						// Envoi de la réponse
						messageURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
								.path("/message")
								.queryParam("chat_id", telegramBotId)
								.queryParam("text", jokeResponse.getAnswer())
								.build().encode().toUri();
						restTemplate.getForObject(messageURL, ApiResponseTelegram.class);
					}
				}
			}
		};
		Timer timer = new Timer("Timer");
		long delay = 2000;
		timer.schedule(task, 0, delay);
	}
}
