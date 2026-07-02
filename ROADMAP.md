# YatraKulo Roadmap

Our vision for **YatraKulo** is to become the premier open-source Personal Wealth & Asset Management Platform. We are evolving from a core foundational ledger into a comprehensive financial tracker that includes cryptocurrencies, stocks, and advanced analytics.

This roadmap outlines our planned progression. Note that timelines are flexible as this is an actively developed open-source project.

## 🟢 Phase 1: Core Foundation (Current)
*Establishing the robust, secure, microservices-based foundation of the platform.*

- [x] Set up Polyglot Microservices Architecture (Java/Spring Boot & TypeScript/NestJS)
- [ ] Configure Kong API Gateway and Lua plugin ecosystem *(In Progress)*
- [x] Implement secure OAuth2 Authentication Service
- [ ] Develop Fiat Ledger Service (Income/Expense tracking) *(In Progress)*
- [x] Implement Account and Transaction services
- [x] Database migrations setup via Flyway
- [ ] Core CI/CD and integration testing pipelines (GitHub Actions/Jenkins)
- [x] Integrate Enterprise Observability Stack (ELK, OpenTelemetry, Prometheus)

## 🟡 Phase 2: Asset Expansion (In Progress / Next up)
*Expanding beyond traditional fiat to track modern asset classes.*

- [ ] **Cryptocurrency Integration:**
  - Track individual crypto wallets and holdings
  - Real-time price aggregation via external APIs
  - Crypto transaction ledger
- [ ] **Stock Market Portfolio:**
  - Track equity portfolios, index funds, and dividends
  - Historical performance charts
- [ ] **Multi-Currency Support:**
  - Dynamic FX rate conversions across the entire portfolio

## 🟠 Phase 3: Analytics & Reporting
*Transforming data into actionable financial insights.*

- [ ] **Reporting Microservice:**
  - Dedicated service for generating complex financial metrics
- [ ] **Automated Insights:**
  - Monthly/weekly summaries and budget vs. actuals alerts
- [ ] **Export Capabilities:**
  - Export reports to PDF, CSV, and Excel formats
- [ ] **Tax Season Prep:**
  - Basic aggregate views designed to simplify tax reporting

## 🔵 Phase 4: Frontend Ecosystem
*Delivering a premium user experience across all devices.*

- [ ] **Web Dashboard:**
  - Modern, responsive SPA using Angular/React/Vue
- [ ] **Mobile Application:**
  - Cross-platform mobile app (Flutter/React Native) for on-the-go tracking
- [ ] **Offline-First Capabilities:**
  - Ensuring the mobile app works seamlessly in low-connectivity areas

---

*Want to help accelerate this roadmap? Check out [CONTRIBUTING.md](CONTRIBUTING.md) to see how you can get involved!*
