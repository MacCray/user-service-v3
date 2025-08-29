package org.intensiv.util;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.intensiv.entity.User;

public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .buildMetadata().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Ошибка инициализации SessionFactory: " + ex);
            StandardServiceRegistryBuilder.destroy(registry);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
