package chweb.ru.services.weather.exceptions;

/**
 * @author chervinko <br>
 * 09.07.2021
 */
public class WeatherException extends Error {
    private final String code;

    public WeatherException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
