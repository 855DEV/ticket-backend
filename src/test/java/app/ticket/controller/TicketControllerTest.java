package app.ticket.controller;

import app.ticket.entity.Ticket;
import app.ticket.repository.ProviderRepository;
import app.ticket.repository.TicketRepository;
import app.ticket.repository.UserRepository;
import app.ticket.setup.TestContext;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@SpringBootTest
@Transactional
@DirtiesContext
@AutoConfigureMockMvc
public class TicketControllerTest {
    @Autowired
    TicketController ticketController;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    UserRepository userRepository;

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
        TestContext.setUpProvider(providerRepository, 10);
        TestContext.createGodAdmin(userRepository);
        authToken = TestContext.getGodAdminAuth(mockMvc);
        assertNotNull(authToken);
        assertNotEquals("", authToken);
    }

    @Test
    @DirtiesContext
    public void findAll() throws Exception {
        // pre-test
        String preStr = mockMvc.perform(get("/ticket?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JSONObject j1 = JSONObject.parseObject(preStr);
        assertEquals(0, j1.getInteger("total"));
        assertTrue(j1.getBooleanValue("last"));
        // insert sample data
        int size = 42;
        for (int i = 0; i < size; i++) {
            Ticket ticket = TestContext.createTicket(providerRepository);
            System.out.println("Ticket: id " + ticket.getId());
            ticketRepository.save(ticket);
        }
        // Test normal page
        String nStr = mockMvc.perform(get("/ticket?page=0&size=10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JSONObject nJson = JSONObject.parseObject(nStr);
        assertEquals(5, nJson.getInteger("total"));
        assertTrue(nJson.getBooleanValue("next"));
        // Test end of page
        String str = mockMvc.perform(get("/ticket?page=100&size=10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JSONObject json = JSONObject.parseObject(str);
        assertTrue(json.getBooleanValue("last"));
        assertFalse(json.getBooleanValue("next"));
    }

    @Test
    @DirtiesContext
    public void findOne() throws Exception {
        Ticket ticket = TestContext.createTicket(providerRepository);
        ticket = ticketRepository.save(ticket);
        assertNotNull(ticket);
        assertNotNull(ticket.getId());
        String res =
                mockMvc.perform(get("/ticket/" + ticket.getId())).andReturn().getResponse().getContentAsString();
        JSONObject json = JSONObject.parseObject(res);
        assertEquals(ticket.getName(), json.getString("name"));
    }

    @Test
    public void insertOne() throws Exception {
        String example = "{\n" +
                "    \"name\": \"上海魔法世界\",\n" +
                "    \"startDate\": \"2020-07-18 10:42:00\",\n" +
                "    \"endDate\": \"2020-08-12 11:00:00\",\n" +
                "    \"providers\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"sections\": [\n" +
                "                {\n" +
                "                    \"description\": \"日场\",\n" +
                "                    \"time\": \"2020-07-18 08:00:00\",\n" +
                "                    \"items\": [\n" +
                "                        {\n" +
                "                            \"description\": \"成人票\",\n" +
                "                            \"price\": 99\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"description\": \"学生票\",\n" +
                "                            \"price\": 49\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"description\": \"夜场\",\n" +
                "                    \"time\": \"2020-07-18 21:30:00\",\n" +
                "                    \"items\": [\n" +
                "                        {\n" +
                "                            \"description\": \"成人票\",\n" +
                "                            \"price\": 49\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"description\": \"学生票\",\n" +
                "                            \"price\": 29\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }, {\n" +
                "            \"id\": 2,\n" +
                "            \"sections\": [\n" +
                "                {\n" +
                "                    \"description\": \"日间场\",\n" +
                "                    \"time\": \"2020-07-18 07:00:00\",\n" +
                "                    \"items\": [\n" +
                "                        {\n" +
                "                            \"description\": \"成人票\",\n" +
                "                            \"price\": 199\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"description\": \"学生票\",\n" +
                "                            \"price\": 149\n" +
                "                        }\n" +
                "                    ]\n" +
                "                },\n" +
                "                {\n" +
                "                    \"description\": \"晚间场\",\n" +
                "                    \"time\": \"2020-07-18 21:40:00\",\n" +
                "                    \"items\": [\n" +
                "                        {\n" +
                "                            \"description\": \"成人票\",\n" +
                "                            \"price\": 149\n" +
                "                        },\n" +
                "                        {\n" +
                "                            \"description\": \"学生票\",\n" +
                "                            \"price\": 129\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        // test request without authorization
        System.out.println("Test without authorization:");
        RequestBuilder request = post("/ticket")
                .contentType(MediaType.APPLICATION_JSON)
                .content(example);
        mockMvc.perform(request).andExpect(status().is(403));
        // test request with authorization
        System.out.println("Test with authorization:");
        assertNotNull(authToken);
        assertNotEquals("", authToken);
        System.out.println("auth token: " + authToken);
        RequestBuilder authedRequest = post("/ticket")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(example);
        String result = mockMvc.perform(authedRequest)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject res = JSONObject.parseObject(result);
        System.out.println(res);
        assertEquals("上海魔法世界", res.getString("name"));

        // bad date format
        String exampleBad = "{\n" +
                "    \"startDate\": \"12\"\n" +
                "}";
        RequestBuilder badReq = post("/ticket")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(exampleBad);
        mockMvc.perform(badReq).andExpect(status().is4xxClientError());
    }
}
