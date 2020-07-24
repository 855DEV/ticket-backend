package app.ticket.controller;

import app.ticket.entity.Ticket;
import app.ticket.repository.ProviderRepository;
import app.ticket.repository.TicketRepository;
import app.ticket.repository.UserRepository;
import app.ticket.setup.TestContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class SearchControllerTest {
    @Autowired
    SearchController searchController;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private String authToken = "";

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        TestContext.setUpProvider(providerRepository, 20);
        Ticket t1 = TestContext.createTicket(providerRepository, "魔法世界","上海交通大学","上海", "20200718", "20200729","mo", true);
        Ticket t2 = TestContext.createTicket(providerRepository, "膜法世界","上交","下海", "20200703", "20200729", "mo", true);
        Ticket t3 = TestContext.createTicket(providerRepository, "模法世界","交通大学","左海", "20200714", "20200729", "ha", true);
        Ticket t4 = TestContext.createTicket(providerRepository, "莫法世界","蛤交大","上海", "20200716", "20200729", "ha", true);
        ticketRepository.save(t1); ticketRepository.save(t2); ticketRepository.save(t3); ticketRepository.save(t4);
    }

    @Test
    public void getSearch() throws Exception {

        // test1
        System.out.println("Test 1: search text=海, orderChosen = 1");
        String result = mockMvc.perform(get("/search?s=海&o=1")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(result);

        // test2
        System.out.println("Test 2: search text=海, orderChosen = 3");
        result = mockMvc.perform(get("/search?s=海&o=3")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(result);

        // test3
        System.out.println("Test 3: search text=世界, city = 上海");
        result = mockMvc.perform(get("/search?s=世界&city=上海")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(result);
    }
}
