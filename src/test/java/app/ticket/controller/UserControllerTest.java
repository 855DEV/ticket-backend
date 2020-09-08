package app.ticket.controller;

import app.ticket.entity.User;
import app.ticket.repository.UserRepository;
import app.ticket.setup.TestContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String api = "/user";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Test
    @DirtiesContext
    void getOne() throws Exception {
        // Test: Not logged in
        mockMvc.perform(get(api)).andExpect(status().is(403));
        // Test: successful login
        Map<String, String> map = TestContext.createUserAuth(mockMvc);
        JSONObject expected = JSONObject.parseObject(map.get("user"));
        String res = mockMvc.perform(get(api).header("Authorization", map.get("auth")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JSONObject json = JSONObject.parseObject(res);
        assertEquals(expected.getString("username"), json.getString("username"));
        assertNull(json.getString(""));
    }

    @Test
    @DirtiesContext
    public void deleteOne() throws Exception {
        // normal delete: delete oneself
        Map<String, Object> data = TestContext.createOneUser(userRepository);
        User user = (User) data.get("user");
        String password = (String) data.get("password");
        System.out.println(user);
        System.out.println("raw: " + password);
        assertNotNull(user);
        assertNotNull(user.getUsername());
        assertNotNull(user.getId());
        assertNotNull(password);
        String auth = TestContext.getUserAuth(mockMvc, user.getUsername(), password);
        assertNotNull(auth);
        JSONObject json = new JSONObject();
        json.put("id", user.getId());
        mockMvc.perform(delete(api).header(TestContext.AUTH_STRING, auth).content(json.toJSONString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // delete request without id in request body
        json.remove("id");
        String res = mockMvc.perform(delete(api).header(TestContext.AUTH_STRING,
                auth).content(json.toJSONString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JSONObject resJson = JSON.parseObject(res);
        assertNotNull(resJson);
        assertEquals(-1, resJson.getInteger("code"));
        // a non-admin user try to delete another user
        json.put("id", 38324);
        res = mockMvc.perform(delete(api).header(TestContext.AUTH_STRING,
                auth).content(json.toJSONString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        resJson = JSON.parseObject(res);
        assertEquals(-1, resJson.getInteger("code"));
        // deletion issued by admin
        TestContext.createGodAdmin(userRepository);
        String adminToken = TestContext.getGodAdminAuth(mockMvc);
        assertNotNull(adminToken);
        user = (User) TestContext.createOneUser(userRepository).get("user");
        assertNotNull(user.getId());
        json = new JSONObject();
        json.put("id", 518);
        // delete non-exist user
        Integer code = JSONObject.parseObject(mockMvc.perform(delete(api).header(TestContext.AUTH_STRING,
                adminToken).content(json.toJSONString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).getInteger("code");
        assertEquals(-1, code);
        json.put("id", user.getId());
        code = JSONObject.parseObject(mockMvc.perform(delete(api).header(TestContext.AUTH_STRING,
                adminToken).content(json.toJSONString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).getInteger("code");
        assertEquals(0, code);
    }

    @Test
    public void register() throws Exception {
        // Register a new user
        String jsonString = "{\n" +
                "    \"username\": \"Kirk\",\n" +
                "    \"password\": \"star_trek\",\n" +
                "    \"nickname\": \"Jim\",\n" +
                "    \"email\": \"wrap@uss.universe\",\n" +
                "    \"phone\": \"123454321\",\n" +
                "    \"address\": \"火星\"\n" +
                "}";
        mockMvc.perform(post(api + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk());
        // username occupied
        String res = mockMvc.perform(post(api + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JSONObject json = JSONObject.parseObject(res);
        assertNotNull(json);
        assertEquals(-1, json.getInteger("code"));
    }

    @Test
    public void updateUserInfo() throws Exception {
        Map<String, Object> userInfo = TestContext.createOneUser(userRepository);
        User user = (User) userInfo.get("user");
        String password = (String)  userInfo.get("password");
        assertNotNull(user);
        // normal operation
        JSONObject reqJson = new JSONObject();
        reqJson.put("id", user.getId());
        reqJson.put("username", "JeanValjean");
        String auth = TestContext.getUserAuth(mockMvc, user.getUsername(),
                password);
        assertNotNull(auth);
        assertNotEquals("", auth);
        mockMvc.perform(put(api).header(TestContext.AUTH_STRING, auth).contentType(MediaType.APPLICATION_JSON).content(reqJson.toJSONString()))
                .andExpect(status().isOk());
        User updatedUser = userRepository.getOne(user.getId());
        assertEquals("JeanValjean", updatedUser.getUsername());
        // update oneself, without specifying one's own id in request JSON
        auth = TestContext.getUserAuth(mockMvc, updatedUser.getUsername(),
                password);
        reqJson.clear();
        reqJson.put("username", "Plato");
        mockMvc.perform(put(api).header(TestContext.AUTH_STRING, auth).contentType(MediaType.APPLICATION_JSON).content(reqJson.toJSONString()))
                .andExpect(status().isOk());
        updatedUser = userRepository.getOne(user.getId());
        assertEquals("Plato", updatedUser.getUsername());
        // unauthorized request
        mockMvc.perform(put(api).content(reqJson.toJSONString()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
