package ee.juhanr.exercise.banking.core.transaction.service;

import ee.juhanr.exercise.banking.core.account.service.AccountService;
import ee.juhanr.exercise.banking.core.balance.entity.BalanceEntity;
import ee.juhanr.exercise.banking.core.balance.service.BalanceService;
import ee.juhanr.exercise.banking.core.currency.service.CurrencyService;
import ee.juhanr.exercise.banking.core.transaction.dto.TransactionCreateRequest;
import ee.juhanr.exercise.banking.core.transaction.dto.TransactionResponse;
import ee.juhanr.exercise.banking.core.transaction.entity.TransactionEntity;
import ee.juhanr.exercise.banking.core.transaction.enums.TransactionDirection;
import ee.juhanr.exercise.banking.core.transaction.mapper.TransactionMapper;
import ee.juhanr.exercise.banking.core.transaction.publisher.TransactionPublisher;
import ee.juhanr.exercise.banking.core.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionPublisher publisher;
    private final TransactionMapper mapper;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final BalanceService balanceService;

    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest request) {
        // Check if the account exists
        accountService.getEntityById(request.getAccountId());
        // Check if the currency exists
        currencyService.getEntityByIsoCode(request.getCurrencyIsoCode());

        // Update the account balance
        BalanceEntity balance;
        if (request.getDirection() == TransactionDirection.IN) {
            balance = balanceService
                    .increaseBalance(request.getAccountId(), request.getAmount(), request.getCurrencyIsoCode());
        } else {
            balance = balanceService
                    .decreaseBalance(request.getAccountId(), request.getAmount(), request.getCurrencyIsoCode());
        }

        // Save the transaction
        TransactionEntity transactionEntity = mapper.toEntity(request, balance.getAmount());
        repository.insert(transactionEntity);
        log.info("Created transaction: id {}, account {}, amount {}, currency {}, direction {}",
                transactionEntity.getId(), transactionEntity.getAccountId(), transactionEntity.getAmount(),
                transactionEntity.getCurrencyIsoCode(), transactionEntity.getDirection());
        publisher.publish(transactionEntity);

        return mapper.toResponse(transactionEntity);
    }

    public List<TransactionResponse> getTransactionsResponse(Long accountId) {
        // Check if the account exists
        accountService.getEntityById(accountId);

        return repository.findByAccountId(accountId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

}
