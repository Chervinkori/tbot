package chweb.ru.handlers;

import chweb.ru.core.Configuration;
import chweb.ru.entity.User;
import chweb.ru.services.UserService;
import chweb.ru.services.weather.WeatherService;
import chweb.ru.services.weather.dto.CurrentWeatherDTO;
import chweb.ru.services.weather.exceptions.WeatherException;
import chweb.ru.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author chervinko <br>
 * 28.06.2021
 */
public class WeatherHandler extends AHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherHandler.class);

    private static final String START_COMMAND = "/start";
    private static final String STOP_COMMAND = "/stop";
    private static final String SUBSCRIBE_COMMAND = "/subscribe";
    private static final String UNSUBSCRIBE_COMMAND = "/unsubscribe";

    private static String START_MSG;
    private static String STOP_MSG;
    private static String SUBSCRIBE_MSG;
    private static String UNSUBSCRIBE_MSG;
    private static String ERROR_MSG;


    @Override
    protected void init() {
        super.init();

        // Инициализация стандартных сообщений
        START_MSG = Configuration.getProperty("bot.msg.start");
        STOP_MSG = Configuration.getProperty("bot.msg.stop");
        SUBSCRIBE_MSG = Configuration.getProperty("bot.msg.subscribe");
        UNSUBSCRIBE_MSG = Configuration.getProperty("bot.msg.unsubscribe");
        ERROR_MSG = Configuration.getProperty("bot.msg.error");
    }

    @Override
    protected void handler(final Message message) throws Exception {
        // Передали локацию
        if (message.getLocation() != null) {
            currentWeatherByLocationHandler(message);
        } else if (message.hasText() && !StringUtils.isEmpty(message.getText())) {
            switch (message.getText()) {
                case START_COMMAND:
                    sendMessage(message.getChatId(), START_MSG);
                    break;
                case STOP_COMMAND:
                    sendMessage(message.getChatId(), STOP_MSG);
                    break;
                case SUBSCRIBE_COMMAND:
                    subscribeHandler(message);
                    break;
                case UNSUBSCRIBE_COMMAND:
                    unsubscribeHandler(message);
                    break;
                // Считаем, что пользователь пытается ввести название города
                default:
                    currentWeatherByCityNameHandler(message);
            }
        }
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    /**
     * Получает текущую погоду по переданной локации.
     */
    private void currentWeatherByLocationHandler(final Message message) throws Exception {
        final Location location = message.getLocation();
        Double lat = location.getLatitude();
        Double lon = location.getLongitude();

        try {
            // Поскольку для каждого клиента отдельный поток
            // нет смысла отправлять запрос в ещё одном отдельном запросе
            final CurrentWeatherDTO currentWeather = WeatherService.getCurrentWeather(lat.toString(), lon.toString());
            // Обновляет координаты у пользователя
            UserService.updateLocation(message.getChat(), lat, lon);
            // Отправляет сообщение
            sendMessage(message.getChatId(), WeatherService.currentWeatherToString(currentWeather));
        } catch (WeatherException exp) {
            sendMessage(message.getChatId(), ERROR_MSG);
        }
    }

    /**
     * Получает текущую погоду по переданной локации.
     */
    private void currentWeatherByCityNameHandler(final Message message) throws Exception {
        String cityName = message.getText();

        CurrentWeatherDTO currentWeather;
        try {
            // Поскольку для каждого клиента отдельный поток
            // нет смысла отправлять запрос в ещё одном отдельном запросе
            currentWeather = WeatherService.getCurrentWeather(cityName);
            // Обновляет координаты у пользователя
            UserService.updateLocation(message.getChat(), currentWeather.getCoord().getLat(), currentWeather.getCoord().getLon());
        } catch (WeatherException exp) {
            if (WeatherService.ERROR_404.equals(exp.getCode())) {
                sendMessage(message.getChatId(), "Город не найден! Проверьте правильность ввода и повторите попытку.");
            } else {
                sendMessage(message.getChatId(), ERROR_MSG);
            }
            return;
        }

        // Отправляет сообщение
        sendMessage(message.getChatId(), WeatherService.currentWeatherToString(currentWeather));
    }

    /**
     * Подписывает пользователя на уведомления.
     */
    private void subscribeHandler(final Message message) {
        // Подписывает пользователя
        final User user = UserService.subscribe(message.getChat());
        if (user.getLat() == null || user.getLon() == null) {
            sendMessage(
                    message.getChatId(),
                    message.getMessageId(),
                    "Для подписки на прогноз погоды отправьте свое местоположение (локацию) или название города."
            );
        } else {
            sendMessage(message.getChatId(), SUBSCRIBE_MSG);
        }
    }

    /**
     * Отписывает пользователя от уведомлений.
     */
    private void unsubscribeHandler(final Message message) {
        // Отписывает пользователя
        UserService.unsubscribe(message.getChat());
        // Отправляет сообщение об успешной отписки
        sendMessage(message.getChatId(), UNSUBSCRIBE_MSG);
    }
}