package ee.juhanr.exercise.banking.integration;

import ee.juhanr.exercise.banking.core.account.dto.AccountCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class AccountIntegrationTest extends IntegrationTestBase {

    private static final long ACCOUNT_ID = 1;
    private static final long CUSTOMER_ID = 100;

    @Test
    @Sql(scripts = {"/db/account/insert-account.sql", "/db/balance/insert-balance.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/db/account/truncate-account.sql", "/db/balance/truncate-balance.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAccount_validAccountId_returnsAccount() {
        given()
                .get("/accounts/{id}", ACCOUNT_ID)
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("id", is((int) ACCOUNT_ID))
                .body("customerId", is((int) CUSTOMER_ID))
                .body("balances", hasSize(1))
                .body("balances", hasItem(Map.of("amount", 0, "currencyIsoCode", "EUR")));
    }

    @Test
    void getAccount_invalidAccountId_returnsNotFound() {
        given()
                .get("/accounts/{id}", ACCOUNT_ID)
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("code", is("RESOURCE_NOT_FOUND"))
                .body("message", is("Account not found: " + ACCOUNT_ID))
                .body("sessionId", notNullValue());
    }

    @Test
    @Sql(statements = "ALTER TABLE account RENAME COLUMN id TO id2;",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "ALTER TABLE account RENAME COLUMN id2 TO id;",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getAccount_databaseError_returnsServerError() {
        given()
                .get("/accounts/{id}", ACCOUNT_ID)
                .then()
                .log().ifValidationFails()
                .statusCode(500)
                .body("code", is("SERVER_ERROR"))
                .body("message", notNullValue())
                .body("sessionId", notNullValue());
    }


    @Test
    @Sql(scripts = {"/db/account/truncate-account.sql", "/db/balance/truncate-balance.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void postAccount_validData_returnsNewAccount() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(asJson(
                        AccountCreateRequest.builder()
                                .customerId(CUSTOMER_ID)
                                .countryIsoCode("EST")
                                .currencies(List.of("EUR", "USD"))
                                .build()))
                .post("/accounts")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .body("id", is((int) ACCOUNT_ID))
                .body("customerId", is((int) CUSTOMER_ID))
                .body("balances", hasSize(2))
                .body("balances", allOf(
                        hasItem(Map.of("amount", 0, "currencyIsoCode", "EUR")),
                        hasItem(Map.of("amount", 0, "currencyIsoCode", "USD"))));
    }

    @Test
    void postAccount_invalidCountry_returnsNotFound() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(asJson(
                        AccountCreateRequest.builder()
                                .customerId(CUSTOMER_ID)
                                .countryIsoCode("XXX")
                                .currencies(List.of("EUR", "USD"))
                                .build()))
                .post("/accounts")
                .then()
                .log().ifValidationFails()
                .statusCode(404)
                .body("code", is("RESOURCE_NOT_FOUND"))
                .body("message", is("Country not found: XXX"))
                .body("sessionId", notNullValue());
    }

    @Test
    void postAccount_noCustomerId_returnsBadRequest() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(asJson(
                        AccountCreateRequest.builder()
                                .customerId(null)
                                .countryIsoCode("XXX")
                                .currencies(List.of("EUR", "USD"))
                                .build()))
                .post("/accounts")
                .then()
                .log().ifValidationFails()
                .statusCode(400)
                .body("code", is("BAD_REQUEST"))
                .body("message", is("field customerId must not be null"))
                .body("sessionId", notNullValue());
    }

}
