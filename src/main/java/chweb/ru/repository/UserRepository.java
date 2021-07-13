package chweb.ru.repository;

import chweb.ru.core.Hibernate;
import chweb.ru.entity.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * @author chervinko <br>
 * 05.07.2021
 */
public class UserRepository {
    /**
     * Поиск пользователя по переданному параметру и значению для него.
     */
    public static User findOneBy(String param, Object value, Session... sessions) {
        Session session = Hibernate.getSession(sessions);

        Transaction transaction = null;
        if (!session.getTransaction().isActive()) {
            transaction = session.beginTransaction();
        }
        try {
            CriteriaQuery<User> query = criteriaFindBy(param, value, session);
            User user = session.createQuery(query).uniqueResult();

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
     * Поиск пользователей по переданному параметру и значению для него.
     */
    public static List<User> findBy(String param, Object value, Session... sessions) {
        Session session = Hibernate.getSession(sessions);

        Transaction transaction = null;
        if (!session.getTransaction().isActive()) {
            transaction = session.beginTransaction();
        }

        try {
            CriteriaQuery<User> query = criteriaFindBy(param, value, session);
            List<User> userList = session.createQuery(query).list();

            if (transaction != null) {
                transaction.commit();
            }

            return userList;
        } catch (Exception exp) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw exp;
        }
    }

    /**
     * Базовые критерии для поиск по параметру.
     */
    private static CriteriaQuery<User> criteriaFindBy(String param, Object value, Session... sessions) {
        Session session = Hibernate.getSession(sessions);
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> from = query.from(User.class);
        query.select(from);
        query.where(builder.equal(from.get(param), value));

        return query;
    }
}
