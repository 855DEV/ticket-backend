package app.ticket.dao;

import app.ticket.entity.Orders;
import app.ticket.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface OrdersDao {
    Orders addOne(Orders order);

    List<Orders> getAllOrdersByUserId(User user);
}
