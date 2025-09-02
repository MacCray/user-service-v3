package org.intensiv.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.intensiv.entity.User;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {
    private final SessionFactory sessionFactory;

    @Override
    public void save(User user) {
        log.debug("Сохранение пользователя: name={}, email={}", user.getName(), user.getEmail());
        try {
            sessionFactory.inTransaction(session -> session.persist(user));
        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при сохранении пользователя name={} email={}", user.getName(), user.getEmail(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Неожиданная ошибка при сохранении пользователя name={} email={}", user.getName(), user.getEmail(), e);
            throw e;
        }
    }

    @Override
    public User findById(Long id) {
        log.debug("Поиск пользователя по id={}", id);
        try {
            User user = sessionFactory.fromSession(session -> session.find(User.class, id));
            return user;
        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при поиске пользователя по id={}", id, e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Неожиданная ошибка при поиске пользователя по id={}", id, e);
            throw e;
        }
    }

    @Override
    public List<User> findAll() {
        log.debug("Получение всех пользователей");
        try {
            List<User> users = sessionFactory.fromSession(session ->
                    session.createSelectionQuery("from User", User.class)
                            .getResultList()
            );
            return users;
        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при получении всех пользователей", e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Неожиданная ошибка при получении всех пользователей", e);
            throw e;
        }
    }

    @Override
    public void update(User user) {
        log.debug("Обновление пользователя id={} name={}", user.getId(), user.getName());
        try {
            sessionFactory.inTransaction(session -> session.merge(user));
        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при обновлении пользователя id={}", user.getId(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Неожиданная ошибка при обновлении пользователя id={}", user.getId(), e);
            throw e;
        }

    }

    @Override
    public void delete(User user) {
        log.debug("Удаление пользователя id={} name={}", user.getId(), user.getName());
        try {
            sessionFactory.inTransaction(session -> session.remove(user));
        } catch (HibernateException e) {
            log.error("Ошибка Hibernate при удалении пользователя id={}", user.getId(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Неожиданная ошибка при удалении пользователя id={}", user.getId(), e);
            throw e;
        }
    }
}
