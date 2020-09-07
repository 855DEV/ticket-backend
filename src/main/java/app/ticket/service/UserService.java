package app.ticket.service;

import app.ticket.entity.User;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;

public interface UserService {
    User getUserByUsername(String username);

    Page<User> findByPage(int pageId, int size);

    User insertOne(JSONObject userJson);

    User insertOne(User user);

    User updateOne(JSONObject userJson);

    boolean deleteOne(Integer userId);

    /**
     * Returns the authenticated User in current context
     */
    User getAuthedUser();

    boolean canDo(Integer targetId);
}
