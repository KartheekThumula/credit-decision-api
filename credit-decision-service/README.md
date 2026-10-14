# Credit Decision Service

A Spring Boot REST API that simulates a transparent credit-line decisioning workflow. It evaluates an applicant's credit score and debt-to-income (DTI) ratio, then returns an approval or decline with a risk tier, credit limit, APR, and human-readable decision reason.

This project demonstrates API design, request validation, automated REST API testing, and CI practices relevant to a credit-domain engineering or quality-engineering role.

## Technology

- Java 17
- Spring Boot 3.2.4
- Maven Wrapper
- JUnit 5 and Rest Assured
- Postman collection
- GitHub Actions CI

## API

### Evaluate a credit application

`POST /api/v1/credit/evaluate`

Example request:

```json
{
  "applicantId": "APP-PRIME-001",
  "creditScore": 780,
  "annualIncome": 120000,
  "requestedAmount": 10000,
  "debtToIncomeRatio": 0.15
}
```

Example response:

```json
{
  "decisionId": "DEC-1A2B3C4D",
  "applicantId": "APP-PRIME-001",
  "status": "APPROVED",
  "approvedLimit": 10000,
  "offeredApr": 14.99,
  "riskTier": "PRIME",
  "reasons": [
    "Approved under Prime tier automatic underwriting"
  ]
}
```

## Decision rules

| Rule | Outcome |
| --- | --- |
| Credit score below 580 | Declined - `SUBPRIME` |
| DTI above 45% | Declined - `HIGH_RISK` |
| Credit score at least 720 and DTI at most 30% | Approved - `PRIME`, full requested limit, 14.99% APR |
| All other valid applications | Approved - `NEAR_PRIME`, limit capped at $2,500, 22.49% APR |

The rules are an intentionally simplified simulation; they are not production lending policy.

## Run locally

From this directory:

```powershell
.\mvnw.cmd spring-boot:run
```

The API starts on `http://localhost:8081`.

## Run tests

```powershell
.\mvnw.cmd test
```

The REST Assured suite runs the application on a random local port and covers:

- smoke/connectivity validation
- prime-tier approval
- subprime decline
- high-DTI decline
- near-prime credit-limit cap
- exact prime-score and DTI boundaries
- invalid-request validation and error response

## API testing with Postman

Import `Credit-Decision-Tests.postman_collection.json` into Postman. Start the application, then run the collection against `http://localhost:8081`.

## CI

The GitHub Actions workflow at `.github/workflows/ci.yml` runs Maven tests on pushes to `main` or `develop` and on pull requests to `main`. It also publishes Surefire test reports as workflow artifacts.

## Future enhancements

- Incorporate annual income into affordability and credit-limit calculations.
- Add OpenAPI documentation, observability, and health checks.
- Externalize decision policy into configuration or a rules engine.
