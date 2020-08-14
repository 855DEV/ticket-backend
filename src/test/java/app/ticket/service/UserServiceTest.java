package app.ticket.service;

import app.ticket.entity.User;
import app.ticket.repository.UserRepository;
import app.ticket.setup.TestContext;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    @Test
    @DirtiesContext
    public void updateOne() {
        Map<String, Object> userData = TestContext.createOneUser(userRepository);
        User user = (User) userData.get("user");
        Integer id = user.getId();
        assertNotNull(id);
        // Normal Case:
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("nickname", "Slipper");
        jsonObject.put("email", "2020@left.by");
        jsonObject.put("phone", "37524052020");
        jsonObject.put("password", "leave");
        userService.updateOne(jsonObject);
        User updatedUser = userRepository.getOne(id);
        assertNotNull(updatedUser);
        assertEquals("Slipper", updatedUser.getNickname());
        assertEquals("37524052020", updatedUser.getPhone());
        // non-exist user
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("id", 8023);
        assertThrows(EntityNotFoundException.class,
                () -> {
                    User userNonExist = userService.updateOne(jsonObject2);
                    assertNull(userNonExist);
                }, "Update operation should fail on a non-exist user.");
    }
}
