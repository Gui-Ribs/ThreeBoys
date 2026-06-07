GRADLE ?= ./gradlew
GRADLE-WINDOWS ?= .\gradlew.bat
COMPOSE ?= docker compose

.PHONY: db-up db-down db-reset run build test fmt clean help

db-up: ## Sobe o container MySQL
	$(COMPOSE) up -d

db-down: ## Para o container MySQL
	$(COMPOSE) down

db-reset: ## Remove containers e volumes
	$(COMPOSE) down -v

run: ## Roda a aplicação JavaFX
	$(GRADLE) :app:run

run-windows: ## Roda a aplicação JavaFX no Windows
	$(GRADLE-WINDOWS) :app:run

build: ## Compila o projeto
	$(GRADLE) build

test: ## Roda os testes
	$(GRADLE) test

fmt: ## Formata o código
	$(GRADLE) :app:fmt

clean: ## Limpa o build
	$(GRADLE) clean

help: ## Lista os comandos
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) \
		| awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}'