package app.ticket.controller;

import app.ticket.entity.Ticket;
import app.ticket.entity.User;
import app.ticket.service.UserService;
import app.ticket.util.Message;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static app.ticket.util.TicketAdapter.wrapTicket;

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
        if (user == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        user.setPassword(""); // remove encrypted password from response
        System.out.println(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/all")
    public JSONObject findAll(@RequestParam(name = "page", required = false) Integer page,
                              @RequestParam(name = "size", required = false) Integer size) {
        System.out.println("GET /user?page=" + page + "&size=" + size);
        page = (page == null) ? 0 : page;
        size = (size == null) ? 10 : size;
        Page<User> userPage = userService.findByPage(page, size);
        List<User> userList = userPage.getContent();
        int total = userPage.getTotalPages();
        boolean next = userPage.hasNext();
        boolean last = userPage.isLast();
        List<JSONObject> content = new ArrayList<>();
        for (User user : userList) {
            content.add(wrapUser(user));
        }
        JSONObject data = new JSONObject();
        data.put("data", content);
        data.put("next", next);
        data.put("last", last);
        data.put("total", total);
        return data;
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

    @PutMapping
    public ResponseEntity<JSONObject> updateUserInfo(@RequestBody JSONObject jsonObject) {
        Integer id = jsonObject.getInteger("id");
        User user = userService.getAuthedUser();
        if (id == null && user != null) {
            id = user.getId();
            jsonObject.put("id", id);
        }
        if (canDo(id)) {
            return ResponseEntity.status(HttpStatus.OK).body(wrapUser(userService.updateOne(jsonObject)));
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
        j.put("blocked", user.getBlocked());
        return j;
    }

    /**
     * Given id, check whether the authed user has permission to operate
     *
     * @param id the id of operation target
     * @return whether the request can perform the operation
     */
    private boolean canDo(Integer id) {
        User user = userService.getAuthedUser();
        Integer ADMIN_TYPE = 0;
        return user != null && (user.getType().equals(ADMIN_TYPE) || user.getId().equals(id));
    }
}
