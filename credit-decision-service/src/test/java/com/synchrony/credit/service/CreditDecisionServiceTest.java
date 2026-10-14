package com.synchrony.credit.service;

import com.synchrony.credit.dto.CreditApplicationRequest;
import com.synchrony.credit.dto.CreditDecisionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditDecisionServiceTest {

    private final CreditDecisionService decisionService = new CreditDecisionService();

    @Test
    @DisplayName("Declines an applicant whose DTI exceeds the 45 percent threshold")
    void declinesHighDebtToIncomeRatio() {
        CreditDecisionResponse response = evaluate(680, "5000.00", 0.46);

        assertEquals("DECLINED", response.getStatus());
        assertEquals("HIGH_RISK", response.getRiskTier());
        assertEquals(BigDecimal.ZERO, response.getApprovedLimit());
        assertEquals(0.0, response.getOfferedApr());
        assertTrue(response.getReasons().get(0).contains("Debt-to-income ratio"));
    }

    @Test
    @DisplayName("Caps a near-prime approval at 2500 dollars")
    void capsNearPrimeApproval() {
        CreditDecisionResponse response = evaluate(680, "5000.00", 0.30);

        assertEquals("APPROVED", response.getStatus());
        assertEquals("NEAR_PRIME", response.getRiskTier());
        assertEquals(new BigDecimal("2500.00"), response.getApprovedLimit());
        assertEquals(22.49, response.getOfferedApr());
    }

    @Test
    @DisplayName("Approves the exact prime score and DTI boundaries")
    void approvesAtPrimeBoundaries() {
        CreditDecisionResponse response = evaluate(720, "6000.00", 0.30);

        assertEquals("APPROVED", response.getStatus());
        assertEquals("PRIME", response.getRiskTier());
        assertEquals(new BigDecimal("6000.00"), response.getApprovedLimit());
        assertEquals(14.99, response.getOfferedApr());
    }

    @Test
    @DisplayName("Approves the exact DTI decline boundary as near-prime")
    void approvesAtHighRiskDtiBoundary() {
        CreditDecisionResponse response = evaluate(680, "2000.00", 0.45);

        assertEquals("APPROVED", response.getStatus());
        assertEquals("NEAR_PRIME", response.getRiskTier());
        assertEquals(new BigDecimal("2000.00"), response.getApprovedLimit());
    }

    private CreditDecisionResponse evaluate(int creditScore, String requestedAmount, double dti) {
        CreditApplicationRequest request = new CreditApplicationRequest(
                "APP-UNIT-001",
                creditScore,
                new BigDecimal("85000.00"),
                new BigDecimal(requestedAmount),
                dti
        );

        return decisionService.evaluateApplication(request);
    }
}
