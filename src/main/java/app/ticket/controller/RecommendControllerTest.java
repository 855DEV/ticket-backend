package app.ticket.controller;

import app.ticket.entity.Ticket;
import app.ticket.entity.TicketItem;
import app.ticket.repository.*;
import app.ticket.service.UserService;
import app.ticket.setup.TestContext;
import com.alibaba.fastjson.JSONArray;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class RecommendControllerTest {
    @Autowired
    RecommendController recommendController;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    TicketProviderRepository ticketProviderRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    TicketItemRepository ticketItemRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    UserService userService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;
    private String authToken;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        TestContext.setUpProvider(providerRepository, 20);
        Ticket t1 = TestContext.createTicket(providerRepository, "魔法世界","上海交通大学","上海", "20200718", "20200729","mo", false);
        Ticket t2 = TestContext.createTicket(providerRepository, "膜法世界","上交","下海", "20200703", "20200729", "mo", false);
        Ticket t3 = TestContext.createTicket(providerRepository, "模法世界","交通大学","左海", "20200714", "20200729", "ha", false);
        Ticket t4 = TestContext.createTicket(providerRepository, "莫法世界","蛤交大","上海", "20200716", "20200729", "ha", false);
        ticketRepository.save(t1); ticketRepository.save(t2); ticketRepository.save(t3); ticketRepository.save(t4);

        TestContext.saveTickets(providerRepository, ticketRepository, 10, 0);

        Map<String, String> mm = TestContext.createUserAuth(mockMvc);
        authToken = mm.get("auth");
        System.out.println("Auth token: " + authToken);
    }

    @Test
    public void getSearch() throws Exception{
        System.out.println("Test 1:");
        List<TicketItem> tl = ticketItemRepository.findAll(), restl = new ArrayList<>();
        Boolean b = false;
        for (TicketItem ti : tl){
            Ticket t = ti.getSection().getTicketProvider().getTicket();
            if (t.getCategory() == "ha")
                restl.add(ti);
            else if (!b){
                b = true;
                restl.add(ti);
            }
        }
        JSONObject postJson = TestContext.getOrderPOSTJSON(restl);
        RequestBuilder authedRequest = post("/order")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postJson.toJSONString());
        String result = mockMvc.perform(authedRequest)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println("post response:" + result);

        System.out.println("n = 5");
        result = mockMvc.perform(get("/recommend?n=5").header("Authorization", authToken)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(result);
        JSONArray ja = (JSONArray) JSONObject.parse(result);
        for (int i = 0; i < ja.size(); i++){
            JSONObject json = ja.getJSONObject(i);
            System.out.println(json.getString("name") + ";  ");
        }
    }
}
