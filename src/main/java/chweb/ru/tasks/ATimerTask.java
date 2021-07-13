package chweb.ru.tasks;

import java.util.Date;
import java.util.TimerTask;

/**
 * @author chervinko <br>
 * 05.07.2021
 */
public abstract class ATimerTask extends TimerTask {
    /**
     * Время первого запуска
     */
    public abstract Date getFirstTime() throws Exception;

    /**
     * Период выполнения
     */
    public abstract long getPeriod() throws Exception;
}
