package app.ticket.dao;

import app.ticket.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDao {
    User insertOne(User user);
    User findOne(Integer id);
    User findUserByUsername(String username);
    Page<User> findByPage(Pageable page);
    User updateOne(User user);
    void deleteOne(Integer userId);
}
