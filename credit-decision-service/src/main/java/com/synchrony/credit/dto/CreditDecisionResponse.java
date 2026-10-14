package com.synchrony.credit.dto;

import java.math.BigDecimal;
import java.util.List;

public class CreditDecisionResponse {
    private String decisionId;
    private String applicantId;
    private String status; // APPROVED, DECLINED, MANUAL_REVIEW
    private BigDecimal approvedLimit;
    private Double offeredApr;
    private String riskTier; // PRIME, NEAR_PRIME, SUBPRIME
    private List<String> reasons;

    public CreditDecisionResponse(String decisionId, String applicantId, String status, 
                                  BigDecimal approvedLimit, Double offeredApr, 
                                  String riskTier, List<String> reasons) {
        this.decisionId = decisionId;
        this.applicantId = applicantId;
        this.status = status;
        this.approvedLimit = approvedLimit;
        this.offeredApr = offeredApr;
        this.riskTier = riskTier;
        this.reasons = reasons;
    }

    // Getters
    public String getDecisionId() { return decisionId; }
    public String getApplicantId() { return applicantId; }
    public String getStatus() { return status; }
    public BigDecimal getApprovedLimit() { return approvedLimit; }
    public Double getOfferedApr() { return offeredApr; }
    public String getRiskTier() { return riskTier; }
    public List<String> getReasons() { return reasons; }
}