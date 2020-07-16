package app.ticket.repository;

import app.ticket.entity.Orders;
import app.ticket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByUser(User user);
}
