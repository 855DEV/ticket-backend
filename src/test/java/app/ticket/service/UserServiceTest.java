package app.ticket.service;

import app.ticket.entity.User;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void insertOne() {
        String jsonUser = "{\n" +
                "    \"username\": \"QDH\",\n" +
                "    \"password\": \"init$pass\",\n" +
                "    \"nickname\": \"1212\",\n" +
                "    \"email\": \"lzq@sjtu.edu.cn\",\n" +
                "    \"phone\": \"18912345678\",\n" +
                "    \"address\": \"火星\"\n" +
                "}";
        JSONObject json = JSONObject.parseObject(jsonUser);
        User user = userService.insertOne(json);
        assertEquals(json.getString("username"), user.getUsername());
    }
}
