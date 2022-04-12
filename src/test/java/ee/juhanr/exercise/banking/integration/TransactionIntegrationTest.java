package ee.juhanr.exercise.banking.integration;

import ee.juhanr.exercise.banking.core.transaction.dto.TransactionCreateRequest;
import ee.juhanr.exercise.banking.core.transaction.enums.TransactionDirection;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class TransactionIntegrationTest extends IntegrationTestBase {

    private static final long ACCOUNT_ID = 1;
    private static final long AMOUNT = 100L;
    private static final String CURRENCY = "EUR";

    @Test
    @Sql(scripts = {"/db/account/insert-account.sql", "/db/balance/insert-balance.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/db/account/truncate-account.sql", "/db/balance/truncate-balance.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getTransactions_validAccountIdWithNoTransactions_returnsEmpty() {
        given()
                .get("/transactions?accountId={id}", ACCOUNT_ID)
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("$", equalTo(Collections.emptyList()));
    }

    @Test
    @Sql(scripts = {"/db/account/insert-account.sql", "/db/balance/insert-balance.sql",
            "/db/transaction/insert-transaction.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/db/account/truncate-account.sql", "/db/balance/truncate-balance.sql",
            "/db/transaction/truncate-transaction.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getTransactions_validAccountIdWithExistingTransactions_returnsTransactions() {
        given()
                .get("/transactions?accountId={id}", ACCOUNT_ID)
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", is(1))
                .body("[0].accountId", is((int) ACCOUNT_ID))
                .body("[0].amount", is((int) AMOUNT))
                .body("[0].currencyIsoCode", is(CURRENCY))
                .body("[0].direction", is("IN"))
                .body("[0].description", is("Test"))
                .body("[0].accountBalanceAfter", is((int) AMOUNT));
    }

    @Test
    void getTransactions_invalidAccountId_returnsNotFound() {
        given()
                .get("/transactions?accountId={id}", ACCOUNT_ID)
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("code", is("RESOURCE_NOT_FOUND"))
                .body("message", is("Account not found: " + ACCOUNT_ID))
                .body("sessionId", notNullValue());
    }


    @Test
    @Sql(scripts = {"/db/account/insert-account.sql", "/db/balance/insert-balance.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/db/account/truncate-account.sql", "/db/balance/truncate-balance.sql",
            "/db/transaction/truncate-transaction.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void postTransaction_validData_returnsNewTransaction() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(asJson(
                        TransactionCreateRequest.builder()
                                .accountId(ACCOUNT_ID)
                                .amount(AMOUNT)
                                .currencyIsoCode(CURRENCY)
                                .direction(TransactionDirection.IN)
                                .description("Test")
                                .build()))
                .post("/transactions")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("id", is(1))
                .body("accountId", is((int) ACCOUNT_ID))
                .body("amount", is((int) AMOUNT))
                .body("currencyIsoCode", is(CURRENCY))
                .body("direction", is("IN"))
                .body("description", is("Test"))
                .body("accountBalanceAfter", is((int) AMOUNT));
    }

    @Test
    void postTransaction_invalidAccountId_returnsNotFound() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(asJson(
                        TransactionCreateRequest.builder()
                                .accountId(ACCOUNT_ID)
                                .amount(AMOUNT)
                                .currencyIsoCode(CURRENCY)
                                .direction(TransactionDirection.IN)
                                .description("Test")
                                .build()))
                .post("/transactions")
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("code", is("RESOURCE_NOT_FOUND"))
                .body("message", is("Account not found: " + ACCOUNT_ID))
                .body("sessionId", notNullValue());
    }

    @Test
    @Sql(scripts = {"/db/account/insert-account.sql", "/db/balance/insert-balance.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/db/account/truncate-account.sql", "/db/balance/truncate-balance.sql",
            "/db/transaction/truncate-transaction.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void postTransaction_outWithInsufficientFunds_returnsBadRequest() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(asJson(
                        TransactionCreateRequest.builder()
                                .accountId(ACCOUNT_ID)
                                .amount(AMOUNT)
                                .currencyIsoCode(CURRENCY)
                                .direction(TransactionDirection.OUT)
                                .description("Test")
                                .build()))
                .post("/transactions")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("code", is("BAD_REQUEST"))
                .body("message", is("Insufficient funds: requested transfer amount 100, existing balance amount 0"))
                .body("sessionId", notNullValue());
    }

}
