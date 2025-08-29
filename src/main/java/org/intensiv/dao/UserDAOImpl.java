package org.intensiv.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.intensiv.entity.User;

import java.util.List;

@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {
    private final SessionFactory sessionFactory;

    @Override
    public void save(User user) {
        try {
            sessionFactory.inTransaction(session -> session.persist(user));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User findById(Long id) {
        try {
            return sessionFactory.fromSession(session -> session.find(User.class, id));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return sessionFactory.fromSession(session ->
                    session.createSelectionQuery("from User", User.class)
                            .getResultList()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public void update(User user) {
        try {
            sessionFactory.inTransaction(session -> session.merge(user));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void delete(User user) {
        try {
            sessionFactory.inTransaction(session -> session.remove(user));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
