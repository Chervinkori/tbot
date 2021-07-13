package chweb.ru;

import chweb.ru.core.Configuration;
import chweb.ru.core.Hibernate;
import chweb.ru.handlers.WeatherHandler;
import chweb.ru.tasks.SendingWeatherTask;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.internal.AbstractServiceRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Timer;

/**
 * @author chervinko <br>
 * 28.06.2021
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    // Обработчик
    public static final WeatherHandler WEATHER_HANDLER = new WeatherHandler();

    public static void main(String[] args) {
        AbstractServiceRegistryImpl serviceRegistry = null;
        try {
            // Инициализация конфигурации
            Configuration.init();
            // Загрузка Hibernate
            serviceRegistry = Hibernate.getServiceRegistry();
            Hibernate.getFactory();
            // Загрузка api бота
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(WEATHER_HANDLER);

            // Запуск таски рассылки
            final SendingWeatherTask sendingWeatherTask = new SendingWeatherTask();
            new Timer().schedule(sendingWeatherTask, sendingWeatherTask.getFirstTime(), sendingWeatherTask.getPeriod());
        } catch (Exception exp) {
            LOGGER.error(exp.getMessage(), exp);
            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
            }
        }
    }
}
