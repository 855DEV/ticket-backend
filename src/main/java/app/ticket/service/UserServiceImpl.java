package app.ticket.service;

import app.ticket.dao.UserDao;
import app.ticket.entity.User;
import com.alibaba.fastjson.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public User insertOne(JSONObject userJson) {
        User user = new User();

        String typeS = userJson.getString("type");
        Integer type;
        type = (typeS == null) ? 1 : Integer.valueOf(typeS);

        String nickname = userJson.getString("nickname");
        String email = userJson.getString("email");
        String phone = userJson.getString("phone");
        String address = userJson.getString("address");
        String username = userJson.getString("username");
        String password = bCryptPasswordEncoder.encode(userJson.getString("password"));
        user.setPassword((user.getPassword()));

        user.setType(type);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setUsername(username);
        user.setPassword(password);

        return userDao.insertOne(user);
    }

    @Override
    public boolean deleteOne(Integer userId) {
        try {
            userDao.deleteOne(userId);
        }catch (Exception e) {
            return false;
        }
        return true;
    }
}
