package com.example.controller;

import com.example.controller.dto.SearchRequestDTO;
import com.example.entity.AccountDetails;
import com.example.entity.AccountSession;
import com.example.entity.types.AccountTrustRating;
import com.example.repository.AccountDetailsRepository;
import com.example.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class MyController {

    private final AccountRepository accountRepository;
    private final AccountDetailsRepository accountDetailsRepository;

    private final EntityManager entityManager;


    @GetMapping("/accounts/me")
    public AccountDetails getMyAccount(@AuthenticationPrincipal AccountSession accountSession) {
        return accountDetailsRepository.findByAccount_Id(accountSession.getAccount().getId());

    }

    @PutMapping("/accounts/me")
    public void updateAccount(@AuthenticationPrincipal AccountSession session,
                              @RequestParam(value = "name", required = false) String name,
                              @RequestParam(value = "country", required = false) String country,
                              @RequestParam(value = "city", required = false) String city) {
        var account = session.getAccount();
        AccountDetails accountDetails = accountDetailsRepository.findByAccount_Id(account.getId());

        if (name != null) {
            account.setName(name);
        }

        if (country != null) {
            accountDetails.setCountry(country);
        }

        if (city != null) {
            accountDetails.setCity(city);
        }

        accountRepository.save(account);
        accountDetailsRepository.save(accountDetails);
    }

    @PostMapping("/accounts/search")
    public List<AccountDetails> searchAccounts(@RequestBody SearchRequestDTO request) {

        Map<String, Object> searchParams = new HashMap<>();
        StringBuilder query = new StringBuilder();

        query.append("from AccountDetails ad where 1 = 1 ");

        if (request.getName() != null) {
            query.append(" and ad.account.name = :name ");
            searchParams.put("name", request.getName());
        }

        if (request.getCountry() != null) {
            query.append(" and ad.country = :country");
            searchParams.put("country", request.getCountry());
        }

        if (request.getCity() != null) {
            query.append(" and ad.city = :city");
            searchParams.put("city", request.getCity());
        }

        if (request.getTrustRating() != null) {
            query.append(" and ad.account.accountTrustRating in (:ratings)");
            var resultingList = new ArrayList<>();

            // Homework
            // Queues, LinkedList
            if (request.getTrustRating() == AccountTrustRating.RED) {
                resultingList.add(AccountTrustRating.RED);
                resultingList.add(AccountTrustRating.ORANGE);
                resultingList.add(AccountTrustRating.GREEN);
            }

            if (request.getTrustRating() == AccountTrustRating.ORANGE) {
                resultingList.add(AccountTrustRating.ORANGE);
                resultingList.add(AccountTrustRating.GREEN);
            }

            if (request.getTrustRating() == AccountTrustRating.GREEN) {
                resultingList.add(AccountTrustRating.GREEN);
            }
            searchParams.put("ratings", resultingList);
        }

        Query emQuery = entityManager.createQuery(query.toString());

//        searchParams.forEach((k, v) -> emQuery.setParameter(k, v));
        searchParams.forEach(emQuery::setParameter);

        return emQuery.getResultList();
    }


}
