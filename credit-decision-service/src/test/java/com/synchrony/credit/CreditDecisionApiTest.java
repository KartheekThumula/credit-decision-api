package com.synchrony.credit;

import com.synchrony.credit.dto.CreditApplicationRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreditDecisionApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @DisplayName("Smoke Test: Ensure API endpoint responds successfully")
    void testEndpointHealthAndConnectivity() {
        CreditApplicationRequest request = new CreditApplicationRequest(
                "APP-100", 750, new BigDecimal("85000"), new BigDecimal("5000"), 0.20
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/credit/evaluate")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    @DisplayName("Functional Test: Prime Customer receives full limit and lowest APR")
    void testPrimeTierApproval() {
        CreditApplicationRequest request = new CreditApplicationRequest(
                "APP-PRIME-001", 780, new BigDecimal("120000"), new BigDecimal("10000"), 0.15
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/credit/evaluate")
        .then()
            .statusCode(200)
            .body("status", equalTo("APPROVED"))
            .body("riskTier", equalTo("PRIME"))
            .body("approvedLimit", equalTo(10000.0f))
            .body("offeredApr", equalTo(14.99f))
            .body("decisionId", startsWith("DEC-"));
    }

    @Test
    @DisplayName("Functional Test: Subprime Score (<580) receives immediate decline")
    void testSubprimeDecline() {
        CreditApplicationRequest request = new CreditApplicationRequest(
                "APP-SUB-002", 540, new BigDecimal("45000"), new BigDecimal("2000"), 0.35
        );

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/api/v1/credit/evaluate")
        .then()
            .statusCode(200)
            .body("status", equalTo("DECLINED"))
            .body("riskTier", equalTo("SUBPRIME"))
            .body("approvedLimit", equalTo(0))
            .body("reasons", hasItem(containsString("FICO score below credit acquisition policy minimum")));
    }

    @Test
    @DisplayName("Negative/Boundary Test: Missing fields and score out of bounds trigger 400 Bad Request")
    void testInvalidRequestPayload() {
        // Credit score 900 exceeds maximum allowable 850
        CreditApplicationRequest invalidRequest = new CreditApplicationRequest(
                "", 900, new BigDecimal("-1000"), new BigDecimal("50"), 1.5
        );

        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
        .when()
            .post("/api/v1/credit/evaluate")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("error", equalTo("Validation Failed"))
            .body("fieldErrors.creditScore", containsString("cannot exceed 850"))
            .body("fieldErrors.applicantId", containsString("must not be blank"));
    }
}