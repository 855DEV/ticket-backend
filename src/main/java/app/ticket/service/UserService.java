package app.ticket.service;

import app.ticket.entity.User;
import com.alibaba.fastjson.JSONObject;

public interface UserService {
    User getUserByUsername(String username);

    User insertOne(JSONObject userJson);

    boolean deleteOne(Integer userId);
    /**
     * Returns the authenticated User in current context
     */
    User getAuthedUser();
}
