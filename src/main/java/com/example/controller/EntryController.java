package com.example.controller;

import com.example.controller.dto.LoginRequestDTO;
import com.example.controller.dto.LoginResponseDTO;
import com.example.entity.Account;
import com.example.entity.AccountSession;
import com.example.repository.AccountSessionRepository;
import com.example.service.PasswordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class EntryController {

    private final PasswordService passwordService;
    private final AccountSessionRepository accountSessionRepository;



    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        Account account = passwordService.getMatchedAccount(request.getUsername(), request.getPassword());
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        AccountSession accountSession = AccountSession.builder()
                .account(account)
                .sessionId(UUID.randomUUID().toString())
                .build();

        accountSessionRepository.save(accountSession);

        return LoginResponseDTO.builder()
                .sessionId(accountSession.getSessionId())
                .build();
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal AccountSession accountSession) {
        accountSessionRepository.delete(accountSession);
    }
}
