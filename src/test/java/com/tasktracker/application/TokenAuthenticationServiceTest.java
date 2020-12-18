package com.tasktracker.application;

import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.Assert.assertNotNull;

//import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;

import org.junit.jupiter.api.Test;


@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TokenAuthenticationServiceTest {

    @Value("${server.port}")
    private int portNumber;

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldNotAllowAccessToUnauthorizedUsers() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/users")).andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldGenerateAuthToken() throws Exception {
       String token = TokenAuthenticationService.createToken("admin");


        assertNotNull(token);
        mvc.perform(MockMvcRequestBuilders.get("/api/users").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }


    @Test
public void existentUserCanGetTokenAndAuthentication() throws Exception {
    String username = "admin";
    String password = "password";

    String body = "{\"username\":" + "\"" + username + "\"," + " \"password\":" + "\""+  password + "\"" + "}";

    MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
            .contentType("application/json").content(body))
            .andExpect(status().isOk()).andReturn();

    JSONObject object = new JSONObject(result.getResponse().getContentAsString());
    String token = object.getString("accessToken");
    System.out.println(token);

    mvc.perform(MockMvcRequestBuilders.get("/api/users")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
    }

    @Test
    public void shouldNotAllowAccessToUnauthUsers() throws Exception {
        String username = "odmen";
        String password = "12345678";
    
        String body = "{\"username\":" + "\"" + username + "\"," + " \"password\":" + "\""+  password + "\"" + "}";
    
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                .contentType("application/json").content(body))
                .andExpect(status().isOk()).andReturn();
    
        JSONObject object = new JSONObject(result.getResponse().getContentAsString());
        String token = object.getString("accessToken");
        System.out.println(token);
    
        mvc.perform(MockMvcRequestBuilders.get("/api/users")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
        }

  }