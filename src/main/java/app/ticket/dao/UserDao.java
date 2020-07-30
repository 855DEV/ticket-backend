package app.ticket.dao;

import app.ticket.entity.*;

public interface UserDao {
    User insertOne(User user);
    User findOne(Integer id);
    User findUserByUsername(String username);
    User updateOne(User user);
    void deleteOne(Integer userId);
}
