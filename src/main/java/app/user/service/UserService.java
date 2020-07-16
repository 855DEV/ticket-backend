package app.user.service;

import app.user.entity.*;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface UserService {
    User insertOne(JSONObject userJson);
}