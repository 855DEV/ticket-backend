package app.user.service;

import app.user.dao.*;
import app.user.entity.*;
import app.ticket.entity.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private OrderDao orderDao;

    @Override
    public Ticket insertOne(JSONObject userJson) {
        User user = new User();

        String typeS = userJsonJson.getString("type");
        Integer type;
        type = (typeS == null) ? 1 : Integer.valueOf(typeS);

        String nickname = userJson.getstring("nickname");
        String email = userJson.getstring("email");
        String phone = userJson.getstring("phone");
        String address = userJson.getstring("address");
        String username = userJson.getstring("username");
        String password = userJson.getstring("password");

        String blockedS = userJsonJson.getString("blocked");
        Integer blocked;
        blocked = (blockedS == null) ? 1 : Integer.valueOf(blockedS);

        user.setType(type);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setUsername(username);
        user.setPassword(password);
        user.setBlocked(blocked);

        return userDao.insertOne(user);
    }
}