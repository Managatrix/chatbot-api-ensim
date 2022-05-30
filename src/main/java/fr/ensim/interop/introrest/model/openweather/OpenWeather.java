package fr.ensim.interop.introrest.model.openweather;

import java.util.List;

public class OpenWeather {

    private List<Weather> weather;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
