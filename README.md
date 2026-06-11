# 3 Boys 

O sistema 3Boys tem como tema e propósito auxiliar no controle de uma loja de doces, levando em conta o gerenciamento de produtos, materiais, clientes e pedidos. A aplicação busca centralizar informações, como estoque de produtos, insumos utilizados na produção, dados dos clientes e registro dos pedidos realizados. 

## Entidades implementadas

- Usuário
- Produto
- Material
- Cliente
- Pedido
- ItemPedido

## Compilar e executar

### 1. Clonar o repositório

```bash
git clone https://github.com/Gui-Ribs/ThreeBoys.git
cd ThreeBoys
```

### 2. Configurar as variáveis de ambiente

Crie um arquivo ```.env``` na raiz do projeto com as configurações do banco, use o ```.env.example```.

### 3. Subir o banco de dados

Para compilar o projeto e subir o banco via docker, usando o make:

```bash
make db-up
```

Os scripts SQL de criação das tabelas e seeds ficam em: ```database/init```. Vide ```03_create_seeds.sql``` para as credênciais de login da aplicação.

### 4. Executar a aplicação

usando o Makefile

```bash
make run
```

Ou diretamente pelo Gradle Wrapper:

```bash
set -a; . ./.env; set +a; ./gradlew :app:run
```

### 5. Ajuda

Para saber outros comandos de execução:

```bash
make help
```

## Instruções

> Caso não utilize o makefile e o docker

1. Instale o Mysql Server;
1. Crie o banco manualmente e Execute os Scripts em ```database/init```;
1. Edite a o arquivo .env.example, faça as alterações que necessitar (Ou deixe como está, só altere o nome para .env);
1. Configure a sua IDE para executar com as envs Ou execute o comando do bloco 4 com o wrapper ou com grandle instalado.