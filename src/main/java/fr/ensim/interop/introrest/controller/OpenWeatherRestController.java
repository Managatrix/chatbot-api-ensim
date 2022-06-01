package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.openweather.City;
import fr.ensim.interop.introrest.model.openweather.OpenWeather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OpenWeatherRestController {

	@Value("${open.weather.api.token}")
	private String openWeatherApiUrl;

	@GetMapping("/meteo")
	public ResponseEntity<OpenWeather> getCoord(
			@RequestParam("cityName") String cityName) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<City[]> responseEntity = restTemplate.getForEntity(
				"http://api.openweathermap.org/geo/1.0/direct?q={cityName}&limit=3"
						+ "&appid=" + openWeatherApiUrl,
				City[].class, cityName);
		City[] cities = responseEntity.getBody();

		if (cities == null) {
			return ResponseEntity.noContent().build();
		} else {
			City city = cities[0];
			System.out.println(city.getName());

			OpenWeather openWeather = restTemplate.getForObject(
					"http://api.openweathermap.org/data/2.5/weather?lat={lat}" + "&lon={lon}&appid="
							+ openWeatherApiUrl,
					OpenWeather.class, city.getLat(), city.getLon());

			return ResponseEntity.ok().body(openWeather);
		}
	}
}
