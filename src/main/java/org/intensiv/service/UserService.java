package org.intensiv.service;

import org.intensiv.dao.UserDAOImpl;
import org.intensiv.entity.User;
import org.intensiv.util.HibernateUtil;

import java.util.List;

public class UserService {
    private final UserDAOImpl userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl(HibernateUtil.getSessionFactory());
    }

    public void createUser(User user) {
        userDAO.save(user);
    }

    public User getUser(Long id) {
        return userDAO.findById(id);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public void updateUser(User user) {
        userDAO.update(user);
    }

    public void deleteUser(User user) {
        userDAO.delete(user);
    }
}
