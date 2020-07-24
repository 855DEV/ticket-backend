package app.ticket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String api = "/user";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOne() throws Exception {
        // Test: User not exist
        mockMvc.perform(get(api + "/1")).andExpect(status().is(403));
    }

    @Test
    public void insertOne() {

    }

}
