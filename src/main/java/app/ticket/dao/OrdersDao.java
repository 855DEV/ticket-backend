package app.ticket.dao;

import app.ticket.entity.Orders;
import app.ticket.entity.User;

import java.util.List;

public interface OrdersDao {
    Orders getOne(Integer orderId);

    Orders addOne(Orders order);

    List<Orders> getAllOrdersByUserId(User user);

    List<Orders> findAll();

    Orders payOne(Orders order);
}
