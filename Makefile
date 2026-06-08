GRADLE ?= ./gradlew
GRADLE-WINDOWS ?= .\gradlew.bat
COMPOSE ?= docker compose

.PHONY: db-up db-down db-reset db-exec run run-windows build test fmt clean help

db-up: ## Sobe o container MySQL
	$(COMPOSE) up -d

db-down: ## Para o container MySQL
	$(COMPOSE) down

db-reset: ## Remove containers e volumes e sobe novamente o MySQL
	$(COMPOSE) down -v
	$(COMPOSE) up -d mysql

db-exec: ## Executa o shell do MySQL no container
	$(COMPOSE) exec mysql sh -lc 'mysql -u"$$MYSQL_USER" -p"$$MYSQL_PASSWORD" "$$MYSQL_DATABASE"'

run: ## Roda a aplicação com variáveis de ambiente
	set -a; . ./.env; set +a; $(GRADLE) :app:run --no-daemon

run-windows: ## Roda a aplicação no Windows
	set -a; . ./.env; set +a; $(GRADLE-WINDOWS) :app:run --no-daemon

build: ## Compila o projeto
	$(GRADLE) build

test: ## Roda os testes unitários
	set -a; . ./.env; set +a; $(GRADLE) :app:test --no-daemon

test-integration: ## Roda testes de integração
	set -a; . ./.env; set +a; $(GRADLE) :app:integrationTest --no-daemon

fmt: ## Formata o código
	$(GRADLE) :app:fmt

clean: ## Limpa o build
	$(GRADLE) clean

help: ## Lista os comandos
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) \
		| awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}'