package app.ticket.service;

import app.ticket.dao.OrdersDao;
import app.ticket.dao.TicketItemDao;
import app.ticket.entity.OrderItem;
import app.ticket.entity.Orders;
import app.ticket.entity.TicketItem;
import app.ticket.entity.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersDao ordersDao;
    private final TicketItemDao ticketItemDao;

    public OrdersServiceImpl(OrdersDao ordersDao, TicketItemDao ticketItemDao) {
        this.ordersDao = ordersDao;
        this.ticketItemDao = ticketItemDao;
    }

    @Override
    public List<Orders> getUserOrders(Integer userId) {
        User user = new User();
        user.setId(userId);
        return ordersDao.getAllOrdersByUserId(user);
    }

    @Override
    public Orders addOne(User user, JSONObject orderJSON) {
        Orders order = new Orders();
        JSONArray itemsJson = orderJSON.getJSONArray("items");
        BigDecimal price = new BigDecimal(0);
        List<OrderItem> items = itemsJson.parallelStream().map((itemJson) -> {
            Integer ticketItemId = ((JSONObject) itemJson).getInteger(
                    "ticketItemId");
            Integer amount = ((JSONObject) itemJson).getInteger("amount");
            TicketItem ticketItem = ticketItemDao.getOne(ticketItemId);
            return new OrderItem(order, ticketItem, amount);
        }).collect(Collectors.toList());
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
        return ordersDao.addOne(order);
    }

    @Override
    public Orders addOne(Orders order) {
        return ordersDao.addOne(order);
    }
}
