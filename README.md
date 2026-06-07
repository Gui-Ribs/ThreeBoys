# 3 Boys 

## Compilar e executar

Para compilar o projeto e subir o banco via docker. Via Makefile:

```bash
db-up: ## Sobe o container MySQL
db-down: ## Para o container MySQL
db-reset: ## Remove containers e volumes
run: ## Roda a aplicação JavaFX
run-windows: ## Roda a aplicação JavaFX no Windows
build: ## Compila o projeto
test: ## Roda os testes
fmt: ## Formata o código
clean: ## Limpa o build
help: ## Lista os comandos
```

Ou diretamente pelo Gradle Wrapper:

```bash
./gradlew run
```