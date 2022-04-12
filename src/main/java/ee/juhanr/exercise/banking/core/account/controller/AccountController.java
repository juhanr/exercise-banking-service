package ee.juhanr.exercise.banking.core.account.controller;

import ee.juhanr.exercise.banking.core.account.dto.AccountCreateRequest;
import ee.juhanr.exercise.banking.core.account.dto.AccountResponse;
import ee.juhanr.exercise.banking.core.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id) {
        return service.getResponseById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestBody @Valid AccountCreateRequest request) {
        return service.createAccount(request);
    }

}
