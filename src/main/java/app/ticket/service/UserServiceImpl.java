package app.ticket.service;

import app.ticket.dao.UserDao;
import app.ticket.entity.User;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserDao userDao,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = userDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public Page<User> findByPage(int pageId, int size) {
        Pageable page = PageRequest.of(pageId, size);
        return userDao.findByPage(page);
    }

    @Override
    public User insertOne(JSONObject userJson) {
        User user = new User();

        String nickname = userJson.getString("nickname");
        String email = userJson.getString("email");
        String phone = userJson.getString("phone");
        String address = userJson.getString("address");
        String username = userJson.getString("username");
        String password = bCryptPasswordEncoder.encode(userJson.getString("password"));
        user.setPassword((user.getPassword()));

        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setUsername(username);
        user.setPassword(password);

        return userDao.insertOne(user);
    }

    @Override
    public User insertOne(User user) {
        return userDao.insertOne(user);
    }

    @Override
    public User updateOne(JSONObject userJson) {
        Integer id = userJson.getInteger("id");
        User user = userDao.findOne(id);
        if (user == null)
            return null;
        userJson.keySet().forEach(keyStr -> {
            String val = userJson.getString(keyStr);
            switch (keyStr) {
                case "nickname":
                    user.setNickname(val);
                    break;
                case "username":
                    user.setUsername(val);
                    break;
                case "email":
                    user.setEmail(val);
                    break;
                case "phone":
                    user.setPhone(val);
                    break;
                case "password":
                    user.setPassword(bCryptPasswordEncoder.encode(val));
                    break;
                case "address":
                    user.setAddress(val);
                    break;
                default:
                    break;
            }
        });
        return userDao.updateOne(user);
    }

    @Override
    public boolean deleteOne(Integer userId) {
        try {
            userDao.deleteOne(userId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public User getAuthedUser() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null)
            return null;
        String username =
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserByUsername(username);
    }

    /**
     * check if current user can perform a privileged operation
     *
     * @param targetId the user id of operation target
     * @return whether the user has privilege
     */
    public boolean canDo(Integer targetId) {
        User user = getAuthedUser();
        if (user == null)
            return false;
        return user.getType().equals(User.ADMIN_TYPE_ID) || user.getId().equals(targetId);
    }
}
