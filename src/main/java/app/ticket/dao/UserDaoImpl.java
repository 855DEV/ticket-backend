package app.ticket.dao;

import app.ticket.entity.*;
import app.ticket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findOne(Integer id) {
        return userRepository.getOne(id);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> findByPage(Pageable page) {
        return userRepository.findAll(page);
    }

    @Override
    public User insertOne(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User updateOne(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteOne(Integer userId) throws IllegalArgumentException {
        userRepository.deleteById(userId);
    }
}
