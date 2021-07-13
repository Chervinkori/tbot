package chweb.ru.services.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author chervinko <br>
 * 09.07.2021
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorDTO {
    @JsonProperty("cod")
    private String cod;
    @JsonProperty("message")
    private String message;

    public String getCod() {
        return cod;
    }

    public String getMessage() {
        return message;
    }
}
