package chweb.ru.tasks;

import chweb.ru.Main;
import chweb.ru.core.Configuration;
import chweb.ru.entity.User;
import chweb.ru.repository.UserRepository;
import chweb.ru.services.weather.WeatherService;
import chweb.ru.services.weather.dto.CurrentWeatherDTO;
import chweb.ru.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chervinko <br>
 * 05.07.2021
 */
public class SendingWeatherTask extends ATimerTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendingWeatherTask.class);

    @Override
    public void run() {
        try {
            final List<User> userList = UserRepository.findBy("subscribe", true);
            if (userList == null || userList.isEmpty())
                return;

            for (User user : userList) {
                new Thread(() -> {
                    try {
                        final CurrentWeatherDTO currentWeatherDTO = WeatherService.getCurrentWeather(user.getLat().toString(), user.getLon().toString());
                        Main.WEATHER_HANDLER.sendMessage(user.getChatId(), WeatherService.currentWeatherToString(currentWeatherDTO));
                    } catch (Exception exp) {
                        LOGGER.error(exp.getMessage(), exp);
                    }
                }).start();
            }
        } catch (Exception exp) {
            LOGGER.error(exp.getMessage(), exp);
        }
    }

    @Override
    public Date getFirstTime() throws Exception {
        String firstTime = Configuration.getProperty("task.sending-weather.first-time");
        if (StringUtils.isEmpty(firstTime)) {
            throw new Exception("The time of the first start is not defined in the configuration");
        }

        LocalTime time;
        try {
            time = LocalTime.parse(firstTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e2) {
            throw new Exception("Incorrect format of the time of the first launch in the configuration");
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, time.getHour());
        today.set(Calendar.MINUTE, time.getMinute());
        today.set(Calendar.SECOND, time.getSecond());

        return today.getTime();
    }

    @Override
    public long getPeriod() throws Exception {
        String firstTime = Configuration.getProperty("task.sending-weather.period");
        if (StringUtils.isEmpty(firstTime)) {
            throw new Exception("Execution period not defined in configuration");
        }

        return TimeUnit.MILLISECONDS.convert(Long.parseLong(firstTime), TimeUnit.MINUTES);
    }
}
