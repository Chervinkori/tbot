package chweb.ru.core;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.internal.AbstractServiceRegistryImpl;

/**
 * @author chervinko <br>
 * 08.07.2021
 */
public final class Hibernate {
    private static AbstractServiceRegistryImpl serviceRegistry;
    private static SessionFactory factory;
    private static Session session;

    public static AbstractServiceRegistryImpl getServiceRegistry() {
        if (serviceRegistry != null && serviceRegistry.isActive()) {
            return serviceRegistry;
        }

        serviceRegistry = (AbstractServiceRegistryImpl) new StandardServiceRegistryBuilder().configure().build();

        return serviceRegistry;
    }

    public static SessionFactory getFactory() {
        if (factory != null && factory.isOpen() && serviceRegistry.isActive()) {
            return factory;
        }

        factory = new MetadataSources(getServiceRegistry()).buildMetadata().buildSessionFactory();

        return factory;
    }

    public static Session getSession() {
        if (session != null && session.isOpen() && factory.isOpen() && serviceRegistry.isActive()) {
            return session;
        }

        SessionFactory factory = getFactory();
        if (factory.getCurrentSession() != null) {
            session = factory.getCurrentSession();
        } else {
            session = factory.openSession();
        }

        return session;
    }

    public static Session getSession(Session... sessions) {
        return sessions == null || sessions.length == 0 || sessions[0] == null ? getSession() : sessions[0];
    }
}
