package com.example.myapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    @BeforeEach
    public void setUp() {
        // clear in-memory storage and reset sequence
        userController.reset();
    }

    @Test
    public void whenGetUsersInitially_thenEmptyList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void whenCreateUser_thenReturnedAndListed() throws Exception {
        User user = new User(null, "Alice");

        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1,\"name\":\"Alice\"}"));

        // after creation, GET should return list with the user
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Alice\"}]"));
    }

    @Test
    public void whenGetByIdNotExist_thenNotFound() throws Exception {
        mockMvc.perform(get("/users/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetByIdAfterCreate_thenReturnUser() throws Exception {
        User user = new User(null, "Bob");
        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"id\":1,\"name\":\"Bob\"}"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Bob\"}"));
    }
}
