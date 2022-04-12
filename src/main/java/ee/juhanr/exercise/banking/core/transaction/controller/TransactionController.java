package ee.juhanr.exercise.banking.core.transaction.controller;

import ee.juhanr.exercise.banking.core.transaction.dto.TransactionCreateRequest;
import ee.juhanr.exercise.banking.core.transaction.dto.TransactionResponse;
import ee.juhanr.exercise.banking.core.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    @GetMapping
    public List<TransactionResponse> getTransactionByAccountId(@RequestParam Long accountId) {
        return service.getTransactionsResponse(accountId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody @Valid TransactionCreateRequest request) {
        return service.createTransaction(request);
    }
}
