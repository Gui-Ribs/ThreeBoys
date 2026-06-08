# 3 Boys 

## Compilar e executar

Para compilar o projeto e subir o banco via docker. Via Makefile:

```bash
db-up: ## Sobe o container MySQL
db-down: ## Para o container MySQL
db-reset: ## Remove containers e volumes
db-exec: ## Executa o shell do MySQL no container
run: ## Roda a aplicação
run-windows: ## Roda a aplicação no Windows usando o .bat
build: ## Compila o projeto
test: ## Roda os testes
fmt: ## Formata o código
clean: ## Limpa o build
help: ## Lista os comandos
```

Ou diretamente pelo Gradle Wrapper:

```bash
set -a; . ./.env; set +a; ./gradlew :app:run
```

## Instruções

> Caso não utilize o makefile e o docker

1. Instale o Mysql Server;
1. Crie o banco manualmente e Execute os Scripts em database/init;
1. Edite a o arquivo .env.example, faça as alterações que necessitar (Ou deixe como está, só altere o nome para .env);
1. Configure a sua IDE para executar com as envs Ou execute o comando do bloco anterior.