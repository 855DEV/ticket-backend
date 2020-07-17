package app.ticket.controller;

import app.ticket.entity.User;
import app.ticket.repository.UserRepository;
import app.ticket.service.UserService;
import app.ticket.util.Message;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody JSONObject jsonObject) {
        System.out.println("POST /register\n" + jsonObject);
        return userService.insertOne(jsonObject);
    }

    @DeleteMapping
    public Object deleteUser(@RequestBody JSONObject jsonObject) {
        System.out.println("DELETE /user");
        Integer delUserId = jsonObject.getInteger("id");
        if (delUserId == null)
            return new Message(-1, "Field id is not specified.");
        // FIXME: change this 2 lines of code to User user = getAuthedUser();
        String username =
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByUsername(username);
        // delete oneself or delete issued by admin
        if (user != null && (jsonObject.getInteger("id").equals(user.getId()) || user.getType().equals(0))) {
            if (!userService.deleteOne(delUserId)) {
                return new Message(-1, "User not found.");
            } else return new Message(0, "OK");
        }
        return new Message(-1, "Access denied.");
    }
}
