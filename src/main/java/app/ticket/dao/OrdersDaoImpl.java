package app.ticket.dao;

import app.ticket.entity.Orders;
import app.ticket.entity.User;
import app.ticket.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrdersDaoImpl implements OrdersDao{
    @Autowired
    private OrdersRepository ordersRepository;

    @Override
    public List<Orders> getAllOrdersByUserId(User user) {
        return ordersRepository.findByUser(user);
    }

    @Override
    public Orders addOne(Orders order) {
        return ordersRepository.save(order);
    }
}
