package org.intensiv.dao;

import org.intensiv.entity.User;

import java.util.List;

public interface UserDAO {
    void save(User user);

    User findById(Long id);

    List<User> findAll();

    void update(User user);

    void delete(User user);
}
