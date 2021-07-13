package chweb.ru.services.weather;

import chweb.ru.core.Configuration;
import chweb.ru.services.weather.dto.CurrentWeatherDTO;
import chweb.ru.services.weather.dto.ErrorDTO;
import chweb.ru.services.weather.exceptions.WeatherException;
import chweb.ru.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);

    // API error codes
    public static final String ERROR_401 = "401";
    public static final String ERROR_404 = "404";
    public static final String ERROR_429 = "429";

    // Unique API key
    private static final String API_KEY_PARAM = "appid";
    // Geographical coordinates
    private static final String LAT_PARAM = "lat";
    private static final String LON_PARAM = "lon";
    // City name
    private static final String Q_PARAM = "q";
    //Units of measurement.
    private static final String UNITS_PARAM = "units";
    // Language
    private static final String LANG_PARAM = "lang";

    // Параметры по умолчанию
    private static final String DEFAULT_UNITS = "metric";
    private static final String DEFAULT_LANG = "ru";

    private static final String CELSIUS = "°C";

    /**
     * Выполняет запрос в погодную Api.
     * Сериализует данные в объект.
     */
    public static <T> T getWeather(String url, Map<String, String> params, Class<T> dtoClass) throws Exception {
        String apiKey = Configuration.getProperty("openweathermap.api-key");
        if (StringUtils.isEmpty(apiKey))
            throw new Exception("Unique API key not set in configuration");

        params.put(API_KEY_PARAM, apiKey);
        params.put(UNITS_PARAM, DEFAULT_UNITS);
        params.put(LANG_PARAM, DEFAULT_LANG);

        T dtoObject;
        ObjectMapper mapper = new ObjectMapper();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url + "?" + StringUtils.getUrlParams(params));
            dtoObject = client.execute(request, httpResponse -> {
                if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    ErrorDTO errorDTO = mapper.readValue(httpResponse.getEntity().getContent(), ErrorDTO.class);
                    WeatherException weatherMapException = new WeatherException(errorDTO.getCod(), errorDTO.getMessage());
                    LOGGER.warn(weatherMapException.getMessage(), weatherMapException);
                    throw weatherMapException;
                }

                return mapper.readValue(httpResponse.getEntity().getContent(), dtoClass);
            });
        }

        return dtoObject;
    }

    /**
     * Получает текущую погоду по координатам.
     */
    public static CurrentWeatherDTO getCurrentWeather(String lat, String lon) throws Exception {
        try {
            String currentUrl = getCurrentUrl();
            if (StringUtils.isEmpty(currentUrl))
                throw new Exception("Current weather url not set in configuration");

            Map<String, String> params = new HashMap<>();
            params.put(LAT_PARAM, lat);
            params.put(LON_PARAM, lon);

            return getWeather(currentUrl, params, CurrentWeatherDTO.class);
        } catch (Exception exp) {
            throw new Exception("Error getting current weather: " + exp.getMessage(), exp);
        }
    }

    /**
     * Получает текущую погоду по имени города.
     */
    public static CurrentWeatherDTO getCurrentWeather(String cityName) throws Exception {
        try {
            String currentUrl = getCurrentUrl();
            if (StringUtils.isEmpty(currentUrl))
                throw new Exception("Current weather url not set in configuration");

            final Map<String, String> params = new HashMap<>();
            params.put(Q_PARAM, cityName);

            return getWeather(currentUrl, params, CurrentWeatherDTO.class);
        } catch (Exception exp) {
            throw new Exception("Error getting current weather: " + exp.getMessage(), exp);
        }
    }

    /**
     * Преобразует объект в строку.
     */
    public static String currentWeatherToString(final CurrentWeatherDTO currentWeatherDTO) {
        final StringBuffer sb = new StringBuffer();

        final CurrentWeatherDTO.Main main = currentWeatherDTO.getMain();
        final List<CurrentWeatherDTO.Weather> weatherList = currentWeatherDTO.getWeather();
        CurrentWeatherDTO.Weather weather = null;
        if (weatherList != null && !weatherList.isEmpty()) {
            weather = weatherList.get(0);
        }

        sb.append("Погода в ").append(currentWeatherDTO.getName()).append(": ");
        if (weather != null && !StringUtils.isEmpty(weather.getDescription())) {
            sb.append(weather.getDescription());
        }

        sb.append("\n").append("Текущая температура: ").append(main.getTemp()).append(CELSIUS);

        sb.append("\n").append("Макс. / мин. температура: ")
                .append(main.getTempMax()).append(CELSIUS)
                .append(" / ").append(main.getTempMin()).append(CELSIUS);

        sb.append("\n").append("Влажность: ").append(main.getHumidity()).append("%");

        CurrentWeatherDTO.Wind wind = currentWeatherDTO.getWind();
        if (wind != null) {
            sb.append("\n").append("Скорость ветра: ").append(wind.getSpeed()).append(" м/с");
        }

        return sb.toString();
    }

    /**
     * Ссылка Api текущей погоды.
     */
    private static String getCurrentUrl() {
        return Configuration.getProperty("openweathermap.current.url");
    }
}
