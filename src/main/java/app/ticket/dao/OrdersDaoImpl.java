package app.ticket.dao;

import app.ticket.entity.Orders;
import app.ticket.entity.User;
import app.ticket.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrdersDaoImpl implements OrdersDao {
    private OrdersRepository ordersRepository;

    public OrdersDaoImpl(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Override
    public Orders getOne(Integer orderId) {
        return ordersRepository.findById(orderId).orElse(null);
    }

    @Override
    public List<Orders> getAllOrdersByUserId(User user) {
        return ordersRepository.findByUser(user);
    }

    @Override
    public Orders addOne(Orders order) {
        return ordersRepository.save(order);
    }

    @Override
    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }
}
