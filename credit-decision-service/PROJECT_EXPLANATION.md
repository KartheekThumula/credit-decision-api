# Credit Decision Service: Simple, Detailed Project Explanation

## 1. The project in one sentence

This project is a small web service that receives a credit-card application, checks a few simple lending rules, and returns a clear decision such as **approved** or **declined**.

Think of it like a careful digital helper at a bank counter. A person gives the helper an application. The helper checks the important numbers, follows the rules, and explains its answer.

## 2. Why this project exists

Banks and credit companies receive many applications for credit cards or credit lines. Each application needs a decision:

- Should the applicant be approved or declined?
- If approved, what is the maximum credit limit?
- What interest rate should be offered?
- Why was that decision made?

Doing this manually for every application would be slow and could lead to inconsistent decisions. This project demonstrates how a software API can make a consistent, quick, and explainable decision.

> Important: The rules in this project are only a learning/demo simulation. They are not real lending policy and should not be used to make real financial decisions.

## 3. The main aim

The main aim is to demonstrate the skills needed to build and test a REST API in a credit-domain setting:

- receive JSON data from another application
- validate that the input is sensible
- apply business rules in Java
- return a structured JSON response with the decision and reason
- test both successful and unsuccessful cases automatically
- run those tests in continuous integration (CI)

## 4. What problem does it solve?

The project addresses three practical problems.

### A. Consistency

The same application details should lead to the same kind of decision every time. The service applies the rules in a fixed order, so it does not guess or change its mind.

### B. Safety of input

An API should not accept impossible or incomplete data. For example, a credit score of `900` is outside the allowed range, and an empty applicant ID is not useful. The project checks these inputs before making a decision.

### C. Clear explanation

A response should say more than just "yes" or "no." This API returns a risk tier, approved limit, APR, and a human-readable reason. That makes the decision easier to understand and test.

## 5. A few important words, explained simply

| Word | Simple meaning |
| --- | --- |
| API | A way for two software programs to communicate with each other. |
| REST API | A common style of web API that uses HTTP requests, similar to visiting a web address. |
| JSON | A simple text format used to send data, like a neatly labelled form. |
| Endpoint | A specific API address. This project uses `/api/v1/credit/evaluate`. |
| `POST` | An HTTP method used to send new data to the server. |
| Validation | Checking whether input is present and within acceptable limits. |
| Credit score | A number used here as one indicator of credit risk. |
| DTI | Debt-to-income ratio. In this demo, it represents how much debt someone has compared with income. |
| APR | Annual Percentage Rate, an annual interest rate. |
| Unit test | A small automated test for one piece of logic. |
| Integration/API test | An automated test that sends a real HTTP request to the application and checks the response. |
| CI | Continuous Integration: automatically running checks when code is pushed to a repository. |

## 6. What did we build?

We built a Java Spring Boot application called **Credit Decision Service**.

It exposes one API endpoint:

```text
POST /api/v1/credit/evaluate
```

The endpoint accepts an application and returns a decision.

### Example application sent to the API

```json
{
  "applicantId": "APP-PRIME-001",
  "creditScore": 780,
  "annualIncome": 120000,
  "requestedAmount": 10000,
  "debtToIncomeRatio": 0.15
}
```

### Example answer returned by the API

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

`decisionId` is generated for every request. It gives each decision a unique reference, which would help trace a decision in a larger real-world system.

## 7. How the application works, step by step

```text
Client or Postman
      |
      | sends a POST request containing JSON
      v
CreditDecisionController
      |
      | asks Spring to validate the request
      v
CreditDecisionService
      |
      | applies the credit-decision rules
      v
CreditDecisionResponse
      |
      | returns JSON with decision, limit, APR, tier, and reason
      v
Client or Postman
```

Here is the same flow in everyday language:

1. A client sends an application to the API.
2. The controller receives it at `/api/v1/credit/evaluate`.
3. Spring validates fields such as the score, requested amount, and DTI.
4. If the request is invalid, the application returns a `400 Bad Request` response with field-by-field errors.
5. If it is valid, the service applies the decision rules.
6. The API sends a `200 OK` response containing the decision and explanation.

## 8. The decision rules

The code checks the rules in this order:

| Rule checked | Result |
| --- | --- |
| Credit score is below `580` | Decline as `SUBPRIME`; limit is `0`; APR is `0.0`. |
| DTI is above `0.45` (45%) | Decline as `HIGH_RISK`; limit is `0`; APR is `0.0`. |
| Credit score is at least `720` **and** DTI is at most `0.30` (30%) | Approve as `PRIME`; give the full requested amount; APR is `14.99`. |
| Any other valid request | Approve as `NEAR_PRIME`; cap the limit at `2500.00`; APR is `22.49`. |

### Why rule order matters

The service checks a low credit score before checking DTI. So if a person has a score below `580` and a high DTI, the response is `SUBPRIME`. This is not an accident; it comes from the order of the rules.

### Boundary examples

- Score `579` is declined as `SUBPRIME` because it is below `580`.
- Score `580` is not declined by the first rule.
- DTI `0.45` is not declined by the high-DTI rule because that rule says **above** `0.45`.
- DTI `0.46` is declined as `HIGH_RISK`.
- Score `720` and DTI `0.30` qualify for `PRIME` because they meet the exact limits.

## 9. Input validation and error handling

Before using the business rules, the API validates input.

| Field | What is checked |
| --- | --- |
| `applicantId` | Must not be blank. |
| `creditScore` | Must be between `300` and `850`. |
| `annualIncome` | Must be greater than zero. |
| `requestedAmount` | Must be at least `100`. |
| `debtToIncomeRatio` | Must be between `0.0` and `1.0`. |

For example, this request is invalid because the ID is blank, score is too high, income is negative, requested amount is too small, and DTI is too high:

```json
{
  "applicantId": "",
  "creditScore": 900,
  "annualIncome": -1000,
  "requestedAmount": 50,
  "debtToIncomeRatio": 1.5
}
```

The API returns HTTP status `400` and identifies the invalid fields. This is better than giving a vague error, because the caller knows what to fix.

## 10. Important project files

| File or folder | Purpose |
| --- | --- |
| `pom.xml` | Maven configuration. It lists Java/Spring/testing dependencies and how the project builds. |
| `CreditDecisionServiceApplication.java` | The starting point that launches the Spring Boot application. |
| `controller/CreditDecisionController.java` | Receives the HTTP request and sends the response. |
| `controller/GlobalExceptionHandler.java` | Turns validation failures into a clean `400` JSON error response. |
| `dto/CreditApplicationRequest.java` | The shape of the incoming application and its validation rules. |
| `dto/CreditDecisionResponse.java` | The shape of the outgoing decision. |
| `service/CreditDecisionService.java` | The main credit-decision business logic. |
| `CreditDecisionApiTest.java` | REST Assured API/integration tests. |
| `service/CreditDecisionServiceTest.java` | Fast unit tests for the decision rules. |
| `Credit-Decision-Tests.postman_collection.json` | Requests that can be imported and run in Postman. |
| `.github/workflows/ci.yml` | GitHub Actions workflow that runs tests automatically. |

## 11. Testing: how we proved the project works

Testing is important because an API can look correct but still return the wrong result for a real situation.

### API/integration tests

The REST Assured tests behave like a client calling the real endpoint. They cover:

- smoke test: the endpoint responds with JSON and HTTP `200`
- prime approval: full limit and the lower APR
- subprime decline: low score is declined
- high-DTI decline: DTI over 45% is declined
- near-prime approval: limit is capped at `2500.00`
- prime boundary: score `720` and DTI `0.30` receive prime approval
- invalid request: validation returns `400` and field errors

### Unit tests

The unit tests call only the decision service, without starting a web server. They are fast and test the core rules directly:

- high-DTI decline
- near-prime cap
- exact prime boundary
- exact `0.45` DTI boundary

The focused unit-test command completed successfully with **4 tests passed**.

### A note about the full API suite in this environment

The full integration suite requires an embedded web server. In this current execution environment, the operating system blocks the loopback connection used to start embedded Tomcat. The test sources compile successfully, but the full HTTP tests must be run in a normal local environment or GitHub Actions. This is an environment limitation, not a known failure of a business rule assertion.

## 12. How to run the project locally

Open a PowerShell window in the project folder and start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

The service starts on:

```text
http://localhost:8081
```

To run all tests:

```powershell
.\mvnw.cmd test
```

To run only the fast decision-rule unit tests:

```powershell
.\mvnw.cmd test "-Dtest=CreditDecisionServiceTest"
```

To call the API manually after it starts:

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8081/api/v1/credit/evaluate" `
  -ContentType "application/json" `
  -Body '{"applicantId":"APP-DEMO-001","creditScore":780,"annualIncome":120000,"requestedAmount":10000,"debtToIncomeRatio":0.15}'
```

## 13. Tools and technologies used

| Tool | Why it is used |
| --- | --- |
| Java 17 | Main programming language. |
| Spring Boot | Helps build and run the web API with less setup. |
| Maven and Maven Wrapper | Downloads dependencies and runs build/test commands consistently. |
| JUnit 5 | Framework for writing automated Java tests. |
| Rest Assured | Makes HTTP API tests readable and expressive. |
| Postman | Lets a person manually send and inspect API requests. |
| Git | Tracks source-code changes. |
| GitHub Actions | Automatically runs Maven tests for pushes and pull requests. |

## 14. What we improved during this project review

1. **Fixed the build configuration.** Rest Assured was listed twice in Maven dependencies. The duplicate dependency was removed, so the project now compiles cleanly.
2. **Added a README.** It gives a quick overview, run instructions, API examples, decision rules, and CI details.
3. **Added direct unit tests.** These make the most important decision rules quick to verify.
4. **Expanded API test cases.** The API suite now covers more realistic approval, decline, and boundary scenarios.

## 15. Honest limitations and next steps

Being clear about limitations is a professional strength. This project deliberately stays small so the core API and testing concepts are easy to understand.

- `annualIncome` is validated but is not yet used to calculate the decision or limit.
- There is no database; decisions are calculated in memory and are not saved.
- There is no authentication because this is a local learning/demo API.
- There is no real external credit bureau or banking integration.
- Real lending systems need stronger security, audit trails, compliance review, fairness testing, policy governance, and carefully approved models/rules.

Useful future improvements would be to include income in affordability calculations, store decisions in a database, document the API with OpenAPI, add health monitoring, and move rules into configuration. These are ideas for discussion, not requirements for this interview project.

## 16. A short interview explanation

You can say this in the interview:

> I built a Spring Boot REST API that simulates a simple credit-decision workflow. The client sends an application as JSON, the API validates the data, and a service applies transparent rules based on credit score and debt-to-income ratio. The response includes the decision, risk tier, approved limit, APR, and a reason. I used Rest Assured for API tests, JUnit for decision-rule unit tests, Postman for manual API testing, and GitHub Actions to run tests in CI. I intentionally kept the scope focused so I could clearly test and explain every rule.

## 17. Questions an interviewer may ask

### Why did you separate controller, service, and DTO classes?

The controller handles HTTP communication, the service contains business rules, and DTOs define the data sent in and out. Separating them keeps each class focused and makes testing easier.

### Why validate requests before applying rules?

Business logic should only run on meaningful input. Validation gives fast, useful feedback and prevents nonsense values from reaching the decision logic.

### Why are both API tests and unit tests useful?

Unit tests are fast and pinpoint rule failures. API tests check that the complete system works together: HTTP request, JSON conversion, validation, controller, service, and response.

### Why test boundary values?

Many bugs happen at edges. A rule saying "above 45%" is different from "45% or above." Boundary tests prove the code behaves correctly at those exact values.

### Why is the decision reason returned?

It makes the result easier for a caller to understand and easier to test. In real financial systems, explainability and traceability are especially important.

### What would you improve next in a real system?

I would externalize the policy, use approved data sources, store audited decisions securely, introduce authentication and authorization, add monitoring, and test security, performance, and fairness requirements.
