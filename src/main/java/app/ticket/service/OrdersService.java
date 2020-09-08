package app.ticket.service;

import app.ticket.entity.Orders;
import app.ticket.entity.User;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface OrdersService {
    List<Orders> getUserOrders(Integer userId);

    Orders getOne(Integer id);

    Orders addOne(User user, JSONObject order);

    Orders addOne(Orders order);

    Orders payOrder(Integer orderId);
}
