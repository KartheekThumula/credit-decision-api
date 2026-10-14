package com.synchrony.credit.service;

import com.synchrony.credit.dto.CreditApplicationRequest;
import com.synchrony.credit.dto.CreditDecisionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreditDecisionService {

    public CreditDecisionResponse evaluateApplication(CreditApplicationRequest request) {
        String decisionId = "DEC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        List<String> reasons = new ArrayList<>();

        // Synchrony Credit Policy Simulation
        if (request.getCreditScore() < 580) {
            reasons.add("FICO score below credit acquisition policy minimum (580)");
            return new CreditDecisionResponse(decisionId, request.getApplicantId(), "DECLINED", BigDecimal.ZERO, 0.0, "SUBPRIME", reasons);
        }

        if (request.getDebtToIncomeRatio() > 0.45) {
            reasons.add("Debt-to-income ratio exceeds risk tolerance threshold (45%)");
            return new CreditDecisionResponse(decisionId, request.getApplicantId(), "DECLINED", BigDecimal.ZERO, 0.0, "HIGH_RISK", reasons);
        }

        if (request.getCreditScore() >= 720 && request.getDebtToIncomeRatio() <= 0.30) {
            reasons.add("Approved under Prime tier automatic underwriting");
            return new CreditDecisionResponse(decisionId, request.getApplicantId(), "APPROVED", request.getRequestedAmount(), 14.99, "PRIME", reasons);
        }

        // Near Prime - Approved with capped limit or elevated APR
        BigDecimal adjustedLimit = request.getRequestedAmount().min(new BigDecimal("2500.00"));
        reasons.add("Approved under Near-Prime tier policy; credit line capped");
        return new CreditDecisionResponse(decisionId, request.getApplicantId(), "APPROVED", adjustedLimit, 22.49, "NEAR_PRIME", reasons);
    }
}