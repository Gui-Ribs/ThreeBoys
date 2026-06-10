START TRANSACTION;

INSERT INTO `user` (name, email, password, role) VALUES
('Administrador', 'admin@3boys.com', 'admin123', 'ADMIN'),
('Funcionário', 'funcionario@3boys.com', 'func123', 'FUNCIONARIO');

SET @user_admin = LAST_INSERT_ID();
SET @user_funcionario = @user_admin + 1;

INSERT INTO produto (nome, chocolate, tipo, preco, qtde, observacao) VALUES
('Brigadeiro Tradicional', 'Chocolate ao leite', 'Brigadeiro', 4.50, 100, 'Produto clássico da casa'),
('Trufa de Morango', 'Chocolate branco', 'Trufa', 6.00, 80, 'Recheio de morango'),
('Barra Recheada', 'Chocolate meio amargo', 'Barra', 12.00, 50, 'Barra artesanal recheada'),
('Ovo de Colher', 'Chocolate ao leite', 'Ovo de Páscoa', 45.00, 20, 'Ovo recheado com brigadeiro'),
('Brownie de Chocolate', 'Chocolate meio amargo', 'Brownie', 8.50, 60, 'Brownie individual');

SET @produto_brigadeiro = LAST_INSERT_ID();
SET @produto_trufa = @produto_brigadeiro + 1;
SET @produto_barra = @produto_brigadeiro + 2;
SET @produto_ovo = @produto_brigadeiro + 3;
SET @produto_brownie = @produto_brigadeiro + 4;

INSERT INTO cliente (nome, telefone, endereco, observacao) VALUES
('Maria Oliveira', '11999990001', 'Rua das Flores, 120', 'Cliente frequente'),
('João Santos', '11999990002', 'Av. Brasil, 450', 'Prefere chocolate meio amargo'),
('Ana Costa', '11999990003', 'Rua Central, 88', NULL),
('Pedro Almeida', '11999990004', 'Rua Norte, 300', 'Retira no local');

SET @cliente_maria = LAST_INSERT_ID();
SET @cliente_joao = @cliente_maria + 1;
SET @cliente_ana = @cliente_maria + 2;
SET @cliente_pedro = @cliente_maria + 3;

INSERT INTO pedido (
    cliente_id,
    valor_total,
    status_pedido,
    data_pedido,
    data_entrega,
    observacao
) VALUES
(@cliente_maria, 15.00, 'PENDENTE', CURRENT_TIMESTAMP, '2026-06-15 14:00:00', 'Pedido pequeno para entrega'),
(@cliente_joao, 24.00, 'CONCLUIDO', CURRENT_TIMESTAMP, '2026-06-12 10:00:00', 'Pedido já entregue'),
(@cliente_ana, 62.00, 'PENDENTE', CURRENT_TIMESTAMP, '2026-06-18 16:30:00', 'Cliente pediu capricho na embalagem'),
(@cliente_pedro, 0.00, 'CANCELADO', CURRENT_TIMESTAMP, NULL, 'Pedido cancelado pelo cliente');

SET @pedido_maria = LAST_INSERT_ID();
SET @pedido_joao = @pedido_maria + 1;
SET @pedido_ana = @pedido_maria + 2;
SET @pedido_pedro = @pedido_maria + 3;

INSERT INTO item_pedido (
    pedido_id,
    produto_id,
    quantidade,
    preco_unitario
) VALUES
(@pedido_maria, @produto_brigadeiro, 2, 4.50),
(@pedido_maria, @produto_trufa, 1, 6.00),
(@pedido_joao, @produto_barra, 2, 12.00),
(@pedido_ana, @produto_ovo, 1, 45.00),
(@pedido_ana, @produto_brownie, 2, 8.50);

COMMIT;