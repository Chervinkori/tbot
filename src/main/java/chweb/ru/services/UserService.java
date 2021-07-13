package chweb.ru.services;

import chweb.ru.core.Hibernate;
import chweb.ru.entity.User;
import chweb.ru.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.telegram.telegrambots.meta.api.objects.Chat;

/**
 * @author chervinko <br>
 * 07.07.2021
 */
public class UserService {
    /**
     * Подписывает пользователя.
     * Если пользователь не найден - создает и подписывает.
     */
    public static User subscribe(final Chat chat) {
        Session session = Hibernate.getSession();

        Transaction transaction = null;
        if (!session.getTransaction().isActive()) {
            transaction = session.beginTransaction();
        }
        try {
            User user = UserRepository.findOneBy("chatId", chat.getId());
            if (user == null) {
                user = createFromChat(chat, session);
            }

            user.setSubscribe(true);
            session.save(user);

            if (transaction != null) {
                transaction.commit();
            }

            return user;
        } catch (Exception exp) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw exp;
        }
    }

    /**
     * Отписывает пользователя.
     */
    public static void unsubscribe(final Chat chat) {
        Session session = Hibernate.getSession();

        Transaction transaction = null;
        if (!session.getTransaction().isActive()) {
            transaction = session.beginTransaction();
        }
        try {
            User user = UserRepository.findOneBy("chatId", chat.getId());
            if (user == null) {
                return;
            }

            user.setSubscribe(false);
            session.save(user);

            if (transaction != null) {
                transaction.commit();
            }
        } catch (Exception exp) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw exp;
        }
    }

    /**
     * Обновляет координаты у пользователя.
     */
    public static void updateLocation(final Chat chat, Double lat, Double lon) {
        Session session = Hibernate.getSession();

        Transaction transaction = null;
        if (!session.getTransaction().isActive()) {
            transaction = session.beginTransaction();
        }
        try {
            User user = UserRepository.findOneBy("chatId", chat.getId());
            if (user == null) {
                user = createFromChat(chat);
            }

            user.setLat(lat);
            user.setLon(lon);
            session.save(user);

            if (transaction != null) {
                transaction.commit();
            }
        } catch (Exception exp) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw exp;
        }
    }

    /**
     * Создает пользователя по данным из чата.
     */
    public static User createFromChat(final Chat chat, Session... sessions) {
        Session session = Hibernate.getSession(sessions);

        Transaction transaction = null;
        if (!session.getTransaction().isActive()) {
            transaction = session.beginTransaction();
        }
        try {
            User user = new User();
            user.setChatId(chat.getId());
            user.setUsername(chat.getUserName());
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            session.save(user);

            if (transaction != null) {
                transaction.commit();
            }

            return user;
        } catch (Exception exp) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw exp;
        }
    }
}