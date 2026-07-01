.PHONY: help dev-account dev-notifications dev-transaction dev-auth dev-ledger

SHELL := /bin/bash

# This sets the default command. If someone just types 'make', it runs 'help'
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "======================================================="
	@echo "🚀 YatraKulo Developer CLI"
	@echo "======================================================="
	@echo "Usage: make [target]"
	@echo ""
	@echo "Node.js (TypeScript) Services:"
	@echo "  dev-account       Start the Account service"
	@echo "  dev-notifications Start the Notifications service"
	@echo "  dev-transaction   Start the Transaction service"
	@echo ""
	@echo "Java (Gradle) Services:"
	@echo "  dev-auth          Start the Authentication service"
	@echo "  dev-ledger        Start the Ledger service"
	@echo "======================================================="

# --- Node.js Services ---

dev-account: ## Run the account service
	@set -a; [ -f services/account/.env ] && source services/account/.env; set +a; \
	pnpm --filter @yk/account start:dev

dev-notifications: ## Run the notifications service
	@set -a; [ -f services/notifications/.env ] && source services/notifications/.env; set +a; \
	pnpm --filter @yk/notifications dev

dev-transaction: ## Run the transaction service
	@set -a; [ -f services/transaction/.env ] && source services/transaction/.env; set +a; \
	pnpm --filter @yk/transaction start:dev

# --- Java Services ---

dev-auth: ## Run the authentication service
	@set -a; [ -f services/authentication/.env ] && source services/authentication/.env; set +a; \
	./gradlew :services:authentication:bootRun

dev-ledger: ## Run the ledger service
	@set -a; [ -f services/ledger/.env ] && source services/ledger/.env; set +a; \
	./gradlew :services:ledger:bootRun

