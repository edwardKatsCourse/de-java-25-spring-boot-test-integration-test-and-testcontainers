package com.example.controller;

import com.example.repository.AccountRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;


class EntryControllerIT extends AbstractIT {

    // testcontainers => start docker   => ~15 seconds
    // spring context => start context  => 5-20 seconds
    // Total                            => 20-35 seconds per controller

    private static final String DEFAULT_USERNAME = "jdoe";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_INCORRECT_PASSWORD = "1234567";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void shouldReturnSessionIdWhenLoginDataIsCorrect() {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/login")
                                .content("""
                                        {
                                            "username": "jdoe",
                                            "password": "123456"
                                        }
                                        """)
                                // Content-Type: application/json
                                .contentType(MediaType.APPLICATION_JSON)
                )
                /**
                 *
                 * json path: $.account.id
                 *
                 * {
                 *  "account": {
                 *      "id": 1
                 *  }
                 * }
                 */
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.sessionId").exists()
                )
                .andExpect(
                        MockMvcResultMatchers.status().is(200)
                )
        ;
    }

    @Test
    @SneakyThrows
    void shouldReturnUnauthorizedWhenLoginDataIsIncorrect() {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .content("""
                                        {
                                            "username": "jdoe",
                                            "password": "1234567"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(
                        MockMvcResultMatchers.status().is(401)
                )
        ;
    }

}

