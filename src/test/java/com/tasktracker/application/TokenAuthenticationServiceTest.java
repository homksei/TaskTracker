package com.tasktracker.application;

import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.Assert.assertNotNull;


import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;

import org.junit.jupiter.api.Test;


@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class TokenAuthenticationServiceTest {


    private String adminTokenValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvZG1lbiIsImlhdCI6MTYwODMzODc5OCwiZXhwIjoxNjA4NDI1MTk4fQ.5SUlCMdX7udblp2jQb8gfOmbtTYAqRGXir_J-brksFrpuaLNva5u9iDxcxE_PcODOZ1z1CQzCsG9AV4qsfeVnA";
    private String userTokenValue = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvZG1lbiIsImlhdCI6MTYwODMyNTA1MywiZXhwIjoxNjA4NDExNDUzfQ.qHIm2y6LmRgTeOaWB8aLapuQ9PZLLUCoHnP-HuH1bVaeCehb4-1RCtqc7pyILgxldyTqsXe5B44m4JwgCR851A";

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

    @Test
    public void newUserCannotBeCreatedByUser() throws Exception {
        String newusername = "odmen";
        String newpassword = "12345678";
        String firstname = "First";
        String lastname = "Last";
        String email = "aaabbb@ddd.ccc";
        String role = "user";
    
        String newUser = "{\"username\":" + "\"" + newusername + "\"," + 
                       "\"firstname\":" + "\"" + firstname + "\"," + 
                      " \"lastname\":" + "\""+  lastname + "\"," + 
                      " \"email\":" + "\""+  email + "\"," + 
                      " \"password\":" + "\""+  newpassword + "\"," + 
                      " \"role\":" + "[\""+  role + "\"]" + "}";
    
        mvc.perform(MockMvcRequestBuilders.post("/api/user_update").contentType("application/json").content(newUser).header("Authorization", "Bearer " + userTokenValue)).andExpect(status().isForbidden());

        }

    public void newUserCanBeCreatedByAdmin() throws Exception {
        String username = "odmen";
        String password = "12345678";
        String firstname = "First";
        String lastname = "Last";
        String email = "aaabbb@ddd.ccc";
        String role = "user";
    
        String body = "{\"username\":" + "\"" + username + "\"," + 
                        "\"firstname\":" + "\"" + firstname + "\"," + 
                        " \"lastname\":" + "\""+  lastname + "\"," + 
                        " \"email\":" + "\""+  email + "\"," + 
                        " \"password\":" + "\""+  password + "\"," + 
                        " \"role\":" + "[\""+  role + "\"]" + "}";

    
        mvc.perform(MockMvcRequestBuilders.post("/user_update").contentType("application/json").content(body).header("Authorization", "Bearer " + adminTokenValue)).andExpect(status().isForbidden());

        }
    

  }