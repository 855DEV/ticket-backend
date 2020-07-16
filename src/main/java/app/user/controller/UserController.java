package app.user.controller;

import app.user.entity.*;
import app.ticket.entity.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/")
public class UserController {
    @Autowired
    UserService userService;
    user currentAccount;


    @PostMapping("/insert")
    public JSONObject register(@RequestBody JSONObject userJson) {
        User user = ticketService.insertOne(userJson);
        System.out.println("register successfully");
        System.out.println(user);
        currentAccount = user;
        return wrapUser(user);
    }

    public JSONObject logout() {
        currentAccount = null;
        return wrapTicket(user);
    }
}
