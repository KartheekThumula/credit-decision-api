package com.synchrony.credit.controller;

import com.synchrony.credit.dto.CreditApplicationRequest;
import com.synchrony.credit.dto.CreditDecisionResponse;
import com.synchrony.credit.service.CreditDecisionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credit")
public class CreditDecisionController {

    private final CreditDecisionService decisionService;

    public CreditDecisionController(CreditDecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<CreditDecisionResponse> evaluateCredit(@Valid @RequestBody CreditApplicationRequest request) {
        CreditDecisionResponse response = decisionService.evaluateApplication(request);
        return ResponseEntity.ok(response);
    }
}