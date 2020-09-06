package app.ticket.service;

import app.ticket.dao.OrdersDao;
import app.ticket.dao.TicketItemDao;
import app.ticket.entity.OrderItem;
import app.ticket.entity.Orders;
import app.ticket.entity.Ticket;
import app.ticket.entity.TicketItem;
import app.ticket.entity.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {"lastResult"})
public class OrdersServiceImpl implements OrdersService {

    private final OrdersDao ordersDao;
    private final TicketItemDao ticketItemDao;

    public OrdersServiceImpl(OrdersDao ordersDao, TicketItemDao ticketItemDao) {
        this.ordersDao = ordersDao;
        this.ticketItemDao = ticketItemDao;
    }

    @Override
    @Cacheable
    public List<Orders> getUserOrders(Integer userId) {
        User user = new User();
        user.setId(userId);
        return ordersDao.getAllOrdersByUserId(user);
    }

    @Override
    public Orders getOne(Integer id) {
        return ordersDao.getOne(id);
    }

    @Override
    public Orders addOne(User user, JSONObject orderJSON) {

        Orders order = new Orders();
        JSONArray itemsJson = orderJSON.getJSONArray("items");
        BigDecimal price = new BigDecimal(0);
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < itemsJson.size(); i++) {
            Integer id = itemsJson.getJSONObject(i).getInteger("ticketItemId");
            Integer amount = itemsJson.getJSONObject(i).getInteger("amount");
            TicketItem ticketItem = ticketItemDao.getOne(id);
            //System.out.println("id: " + id + " amount: " + amount + "\n price: " + ticketItem.getPrice());
            items.add(new OrderItem(order, ticketItem, amount));
        }
        // calculate the total price
        for (OrderItem i : items) {
            TicketItem t = i.getTicketItem();
            BigDecimal tmp =
                    t.getPrice().multiply(new BigDecimal(i.getAmount()));
            price = price.add(tmp);
        }

        order.setOrderItemList(items);
        order.setUser(user);
        order.setPrice(price);
        order.setTime(new Date());  // current datetime
        System.out.println("add One ready. " + order);

        Orders ret = ordersDao.addOne(order);
        System.out.println("current size: " + ordersDao.getAllOrdersByUserId(user).size());

        return ret;
    }

    @Override
    public Orders addOne(Orders order) {
        return ordersDao.addOne(order);
    }

    @Override
    public Orders payOrder(Integer orderId) {
        Orders order = ordersDao.getOne(orderId);
        order.setState(1);
        return order;
    }

}
