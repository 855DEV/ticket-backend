package app.ticket.service;

import app.ticket.entity.Orders;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface OrdersService {
    List<Orders> getUserOrders(Integer userId);

    Orders addOne(JSONObject order);
}
