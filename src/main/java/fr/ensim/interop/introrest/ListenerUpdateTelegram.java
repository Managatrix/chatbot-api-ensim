package fr.ensim.interop.introrest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fr.ensim.interop.introrest.model.joke.Joke;
import fr.ensim.interop.introrest.model.openweather.City;
import fr.ensim.interop.introrest.model.openweather.OpenWeather;
import fr.ensim.interop.introrest.model.telegram.ApiResponseTelegram;
import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
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

					List<String> words = Arrays.asList(messageText.split("[ ]+"));

					if ((words.size() >= 2 && words.get(1).equalsIgnoreCase("joke"))
							|| words.get(0).equalsIgnoreCase("joke")) {
						Boolean isGoodJokeRequested = true;
						if (words.get(0).equalsIgnoreCase("bad")) {
							// Demande de blague nulle
							isGoodJokeRequested = false;

						}
						// if (words.get(0).equalsIgnoreCase("good") ||
						// words.get(0).equalsIgnoreCase("funny")
						// || words.get(0).equalsIgnoreCase("joke")) {
						// // Demande de bonne blague
						// isGoodJokeRequested = true;
						// }

						URI jokeURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
								.path("/randomJoke")
								.queryParam("isGoodJokeRequested", isGoodJokeRequested)
								.build().encode().toUri();

						Joke jokeResponse = restTemplate.getForObject(jokeURL, Joke.class);

						// Envoi de la question
						URI messageURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
								.path("/message")
								.queryParam("chat_id", telegramBotId)
								.queryParam("text", "(" + jokeResponse.getRating() + ") - " + jokeResponse.getTitle())
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

					if (words.get(0).equalsIgnoreCase("meteo")) {
						StringJoiner joiner = new StringJoiner(" ");
						for (int i = 1; i < words.size(); i++) {
							joiner.add(words.get(i));
						}
						String meteoRequestBody = joiner.toString();

						// meteo(city) request
						URI meteoURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
								.path("/meteo")
								.queryParam("cityName", meteoRequestBody)
								.build().encode().toUri();
						OpenWeather openWeather = restTemplate.getForObject(meteoURL, OpenWeather.class);

						// searchCity(city) request
						URI cityURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
								.path("/searchCity")
								.queryParam("cityName", meteoRequestBody)
								.build().encode().toUri();
						City cityResponse = restTemplate.getForObject(cityURL, City.class);

						String meteoResponseMessage;
						if (openWeather == null || cityResponse == null) {
							// If not cities were found
							meteoResponseMessage = "Requested city was not found :/";
						} else {
							meteoResponseMessage = "Mitiyo for " + cityResponse.getName() + ", "
									+ openWeather.getSys().getCountry() + "\n\t- Today: "
									+ openWeather.getWeather().get(0).getMain() + " ("
									+ openWeather.getWeather().get(0).getDescription() + ") | Temperature : "
									+ (int) (openWeather.getMain().getTemp() - 273.15) + " C";
						}

						// sendMessage(text) request
						URI messageURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
								.path("/message")
								.queryParam("chat_id", telegramBotId)
								.queryParam("text", meteoResponseMessage)
								.build().encode().toUri();
						restTemplate.getForObject(messageURL, ApiResponseTelegram.class);
					}

					if (words.get(0).equalsIgnoreCase("help")) {
						// sendMessage(text) request
						URI messageURL = UriComponentsBuilder.fromUriString("http://127.0.0.1:9090")
								.path("/message")
								.queryParam("chat_id", telegramBotId)
								.queryParam("text", "Available commands :\n\t- (bad or good) joke\n\t- meteo [city]\n")
								.build().encode().toUri();
						restTemplate.getForObject(messageURL, ApiResponseTelegram.class);
					}
				}
			}
		};
		Timer timer = new Timer("Timer");
		long delay = 500;
		timer.schedule(task, 0, delay);
	}
}
