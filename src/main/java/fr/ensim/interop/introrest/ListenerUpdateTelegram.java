package fr.ensim.interop.introrest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

		// ResponseEntity<ApiResponseTelegram> responseTelegram = messageRestController
		// .sendMessage(Long.parseLong(telegramBotId), "Hi");
		// ResponseEntity<ApiResponseTelegram> responseTelegram = messageRestController
		// .getUpdates();

		TimerTask task = new TimerTask() {
			int offset = -1;
			int updateNum = 0;

			public void run() {
				// Operation de pooling pour capter les evenements Telegram
				URI targetUrl = UriComponentsBuilder.fromUriString(telegramApiUrl)
						.path("/getUpdates")
						.queryParam("offset", offset)
						.build().encode().toUri();
				ApiResponseUpdateTelegram response = restTemplate.getForObject(targetUrl,
						ApiResponseUpdateTelegram.class);
				System.out.println("----------- Get Updates num." + updateNum++);

				if (response != null && !response.getResult().isEmpty()) {
					String messageText = response.getResult().get(0).getMessage().getText();
					offset = response.getResult().get(0).getUpdateId();
					System.out.println("New offset = " + ++offset);
					System.out.println("messageText = " + messageText);
					// Traiter message reçu
				}
			}
		};
		Timer timer = new Timer("Timer");
		long delay = 2000;
		timer.schedule(task, 0, delay);
	}
}
