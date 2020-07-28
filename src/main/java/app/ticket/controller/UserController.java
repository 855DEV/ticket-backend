package app.ticket.controller;

import app.ticket.entity.User;
import app.ticket.service.UserService;
import app.ticket.util.Message;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUserInfo() {
        User user = userService.getAuthedUser();
        if(user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        user.setPassword(""); // remove encrypted password from response
        System.out.println(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody JSONObject jsonObject) {
        System.out.println("POST /register\n" + jsonObject);
        User tmp = userService.getUserByUsername(jsonObject.getString("username"));
        if (tmp != null) {
            JSONObject m = new JSONObject();
            m.put("code", -1);
            m.put("message", "Username has been registered.");
            return ResponseEntity.status(HttpStatus.OK).body(m.toJSONString());
        }
        User user = userService.insertOne(jsonObject);
        return ResponseEntity.ok(wrapUser(user).toJSONString());
    }

    @DeleteMapping
    public Object deleteUser(@RequestBody JSONObject jsonObject) {
        System.out.println("DELETE /user");
        Integer delUserId = jsonObject.getInteger("id");
        if (delUserId == null)
            return new Message(-1, "Field id is not specified.");
        User user = userService.getAuthedUser();
        // delete oneself or delete issued by admin
        if (user != null && (jsonObject.getInteger("id").equals(user.getId()) || user.getType().equals(0))) {
            if (!userService.deleteOne(delUserId)) {
                return new Message(-1, "User not found.");
            } else return new Message(0, "OK");
        }
        return new Message(-1, "Access denied.");
    }

    private JSONObject wrapUser(User user) {
        if (user == null) return null;
        JSONObject j = new JSONObject();
        j.put("id", user.getId());
        j.put("type", user.getType());
        j.put("nickname", user.getNickname());
        j.put("email", user.getEmail());
        j.put("phone", user.getPhone());
        j.put("address", user.getAddress());
        j.put("username", user.getUsername());
        j.put("password", user.getPassword());
        j.put("blocked", user.getBlocked());
        return j;
    }
}
