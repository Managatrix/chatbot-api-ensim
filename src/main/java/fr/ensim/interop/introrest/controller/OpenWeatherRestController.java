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
	private String openWeatherApiToken;
	// @Value("${open.weather.api.url}")
	// private String openWeatherApiUrl;

	@GetMapping("/meteo")
	public ResponseEntity<OpenWeather> meteoFromCity(
			@RequestParam("cityName") String cityName) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<City[]> responseEntity = restTemplate.getForEntity(
				"http://api.openweathermap.org/geo/1.0/direct?q={cityName}&limit=3"
						+ "&appid=" + openWeatherApiToken,
				City[].class, cityName);
		City[] cities = responseEntity.getBody();

		if (cities == null || cities.length == 0) {
			return ResponseEntity.noContent().build();
		} else {
			City city = cities[0];
			// System.out.println(city.getName());

			OpenWeather openWeather = restTemplate.getForObject(
					"https://api.openweathermap.org/data/2.5/weather?lat={lat}" + "&lon={lon}&appid="
							+ openWeatherApiToken,
					OpenWeather.class, city.getLat(), city.getLon());

			return ResponseEntity.ok().body(openWeather);
		}
	}

	@GetMapping("/searchCity")
	public ResponseEntity<City> searchCity(
			@RequestParam("cityName") String cityName) {

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<City[]> responseEntity = restTemplate.getForEntity(
				"http://api.openweathermap.org/geo/1.0/direct?q={cityName}&limit=1" + "&appid=" + openWeatherApiToken,
				City[].class, cityName);
		City[] cities = responseEntity.getBody();

		if (cities == null || cities.length == 0) {
			return ResponseEntity.noContent().build();
		} else {
			City city = cities[0];
			return ResponseEntity.ok().body(city);
		}
	}
}
