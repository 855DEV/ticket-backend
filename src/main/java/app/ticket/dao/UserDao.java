package app.ticket.dao;

import app.ticket.entity.*;

public interface UserDao {
    User insertOne(User user);
    User findUserByUsername(String username);
}
