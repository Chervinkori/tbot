package chweb.ru.handlers;

import chweb.ru.core.Configuration;
import chweb.ru.utils.StringUtils;
import org.slf4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author chervinko <br>
 * 28.06.2021
 */
public abstract class AHandler extends TelegramLongPollingBot {
    // Флаг инициализации обработчика
    private static boolean initFlag = false;

    /**
     * Обработчик сообщения.
     */
    protected abstract void handler(final Message message) throws Exception;

    /**
     * Логировщик.
     */
    protected abstract Logger getLogger();

    @Override
    public void onUpdateReceived(Update update) {
        // Подгрузка доп. данных (выполняется один раз)
        if (!initFlag) {
            init();
            initFlag = true;
        }

        new Thread(() -> {
            try {
                final Message message = update.getMessage();
                if (message == null)
                    return;

                // Обработка
                handler(message);
            } catch (Exception exp) {
                getLogger().error("Error processing messages: " + exp.getMessage(), exp);
            }
        }).start();
    }

    /**
     * Доп. инициализация обработчика (выполняется один раз).
     */
    protected void init() {
    }

    /**
     * Отправляет сообщение в чат.
     */
    public void sendMessage(Long chatId, Integer replyToMessageId, String msg) {
        if (chatId == null || StringUtils.isEmpty(msg)) {
            return;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyToMessageId(replyToMessageId);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(msg);

        try {
            execute(sendMessage);
        } catch (TelegramApiException exp) {
            throw new Error(exp);
        }
    }

    /**
     * Отправляет сообщение в чат.
     */
    public void sendMessage(Long chatId, String msg) {
        if (chatId == null || StringUtils.isEmpty(msg)) {
            return;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(msg);

        try {
            execute(sendMessage);
        } catch (TelegramApiException exp) {
            throw new Error(exp);
        }
    }

    @Override
    public String getBotUsername() {
        return Configuration.getProperty("bot.username");
    }

    @Override
    public String getBotToken() {
        return Configuration.getProperty("bot.token");
    }
}
