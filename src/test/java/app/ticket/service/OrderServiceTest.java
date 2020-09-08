package app.ticket.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    private OrdersService ordersService;

    @Test
    public void testGetOrder() {
        ordersService.getOne(1);
    }
}
