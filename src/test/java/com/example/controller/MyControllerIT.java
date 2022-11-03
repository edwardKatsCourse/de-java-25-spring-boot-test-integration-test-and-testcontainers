package com.example.controller;

import com.example.entity.AccountSession;
import com.example.repository.AccountRepository;
import com.example.repository.AccountSessionRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

class MyControllerIT extends AbstractIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountSessionRepository accountSessionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private AccountSession saveAndGetAccountSession() {
        var accountSession = AccountSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .account(accountRepository.findByUsername("jdoe"))
                .build();

        return accountSessionRepository.save(accountSession);
    }

    @Test
    @SneakyThrows
    void shouldReturnAccountDetailsWhenLoggedIn() {
        AccountSession accountSession = saveAndGetAccountSession();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/accounts/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", accountSession.getSessionId())
                )
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath("$.account.id")
                                .value(accountSession.getAccount().getId())
                );
    }

    @Test
    @SneakyThrows
    void shouldReturnForbiddenWhenNoSuchSessionId() {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/accounts/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "dummy_session_id")
                )
                .andExpect(
                        MockMvcResultMatchers.status().is(403)
                )
                .andExpect(
                        MockMvcResultMatchers
                                .jsonPath("$.account.id").doesNotExist()
                )

        ;
    }

}
