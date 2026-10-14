package com.synchrony.credit.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreditApplicationRequest {

    @NotBlank(message = "Applicant ID must not be blank")
    private String applicantId;

    @NotNull(message = "Credit score is mandatory")
    @Min(value = 300, message = "Credit score cannot be lower than 300")
    @Max(value = 850, message = "Credit score cannot exceed 850")
    private Integer creditScore;

    @NotNull(message = "Annual income is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Annual income must be positive")
    private BigDecimal annualIncome;

    @NotNull(message = "Requested limit is mandatory")
    @DecimalMin(value = "100.0", message = "Minimum requested limit is 100")
    private BigDecimal requestedAmount;

    @NotNull(message = "Debt-to-Income (DTI) ratio is mandatory")
    @DecimalMin(value = "0.0", message = "DTI must be non-negative")
    @DecimalMax(value = "1.0", message = "DTI cannot exceed 1.0 (100%)")
    private Double debtToIncomeRatio;

    public CreditApplicationRequest() {}

    public CreditApplicationRequest(String applicantId, Integer creditScore, BigDecimal annualIncome, BigDecimal requestedAmount, Double debtToIncomeRatio) {
        this.applicantId = applicantId;
        this.creditScore = creditScore;
        this.annualIncome = annualIncome;
        this.requestedAmount = requestedAmount;
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    // Getters and Setters
    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
    public BigDecimal getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
    public Double getDebtToIncomeRatio() { return debtToIncomeRatio; }
    public void setDebtToIncomeRatio(Double debtToIncomeRatio) { this.debtToIncomeRatio = debtToIncomeRatio; }
}