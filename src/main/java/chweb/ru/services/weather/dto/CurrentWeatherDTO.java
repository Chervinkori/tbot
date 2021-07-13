package chweb.ru.services.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author chervinko <br>
 * 07.07.2021
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeatherDTO {
    @JsonProperty("coord")
    private Coord coord;
    @JsonProperty("weather")
    private List<Weather> weather;
    @JsonProperty("base")
    private String base;
    @JsonProperty("main")
    private Main main;
    @JsonProperty("visibility")
    private int visibility;
    @JsonProperty("wind")
    private Wind wind;
    @JsonProperty("clouds")
    private Clouds clouds;
    @JsonProperty("dt")
    private int dt;
    @JsonProperty("sys")
    private Sys sys;
    @JsonProperty("timezone")
    private int timezone;
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("cod")
    private int cod;

    public Coord getCoord() {
        return coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public String getBase() {
        return base;
    }

    public Main getMain() {
        return main;
    }

    public int getVisibility() {
        return visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public int getDt() {
        return dt;
    }

    public Sys getSys() {
        return sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCod() {
        return cod;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coord {
        @JsonProperty("lon")
        private double lon;
        @JsonProperty("lat")
        private double lat;

        public double getLon() {
            return lon;
        }

        public double getLat() {
            return lat;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        @JsonProperty("id")
        private int id;
        @JsonProperty("main")
        private String main;
        @JsonProperty("description")
        private String description;
        @JsonProperty("icon")
        private String icon;

        public int getId() {
            return id;
        }

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        @JsonProperty("temp")
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
        @JsonProperty("pressure")
        private int pressure;
        @JsonProperty("humidity")
        private int humidity;

        public double getTemp() {
            return temp;
        }

        public double getFeelsLike() {
            return feelsLike;
        }

        public double getTempMin() {
            return tempMin;
        }

        public double getTempMax() {
            return tempMax;
        }

        public int getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        @JsonProperty("speed")
        private double speed;
        @JsonProperty("deg")
        private int deg;
        @JsonProperty("gust")
        private double gust;

        public double getSpeed() {
            return speed;
        }

        public int getDeg() {
            return deg;
        }

        public double getGust() {
            return gust;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clouds {
        @JsonProperty("all")
        private int all;

        public int getAll() {
            return all;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        @JsonProperty("type")
        private int type;
        @JsonProperty("id")
        private int id;
        @JsonProperty("country")
        private String country;
        @JsonProperty("sunrise")
        private int sunrise;
        @JsonProperty("sunset")
        private int sunset;

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public String getCountry() {
            return country;
        }

        public int getSunrise() {
            return sunrise;
        }

        public int getSunset() {
            return sunset;
        }
    }
}
