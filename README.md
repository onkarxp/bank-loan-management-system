Enterprise Bank Loan Management System
A comprehensive, end-to-end backend REST API engineered with Spring Boot and Spring Data JPA to manage the complete lifecycle of banking loans. This system moves beyond standard CRUD operations to handle complex financial logic, strict transactional security, and high-performance data aggregation.

Core System Modules:

Customer Onboarding & KYC: Features secure profile management with unique-constraint validations and an administrative KYC verification pipeline.

Loan Origination: An automated factory module that processes principal amounts, interest rates, and tenures to generate mathematically exact, amortized repayment schedules.

Dynamic Repayment Engine: A highly secure transaction processor that utilizes BigDecimal for zero-loss financial accuracy. It features dynamic, RAM-based penalty calculations for overdue EMIs, ensuring database cleanliness and real-time accuracy.

Enterprise Reporting: An optimized administrative module that utilizes Java 8 Streams and Map-Reduce patterns to perform bulk financial aggregations, successfully eliminating N+1 database bottlenecks.
