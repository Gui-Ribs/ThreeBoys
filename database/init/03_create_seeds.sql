START TRANSACTION;

INSERT INTO `user` (name, email, password, role) VALUES
('Mijaro Nakama', 'admin@3boys.com', '$2a$10$4CaB.rpXxVh4f1EUfpx4jeORvYyYCWVLcEvx638eE/zzEt07byb0W', 'ADMIN'), -- admin123
('Takaro Nakama', 'funcionario@3boys.com', '$2a$10$uQhZ1ZYIsKVlFgQE7y792.6o1fqJHPcnY1Nil.AOjk1CcQyR2CQhC', 'FUNCIONARIO'); -- func123

SET @user_admin = LAST_INSERT_ID();
SET @user_funcionario = @user_admin + 1;

INSERT INTO produto (nome, tamanho, chocolate, tipo, preco, qtde, observacao) VALUES
('Ovo', '100g','Ao leite', 'Brigadeiro', 10.50, 100, 'Produto clássico da casa'),
('Trufa', 'padrao', 'Ao Leite', 'Normal', 6.00, 80, 'Recheio de morango'),
('Barra Recheada', 'medio','50%', 'Trufado', 12.00, 10, 'Barra artesanal recheada com prestigio'),
('Ovo', '300g', 'Ao leite', 'Colher', 45.00, 5, 'Ovo recheado com brigadeiro'),
('Pirulito', 'grande', 'Branco', 'Crocante', 8.50, 60, 'Crocante de flocos de arroz');

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

INSERT INTO material (
    nome,
    qtde,
    unidade_medida,
    marca,
    preco,
    estoque,
    descricao
) VALUES
('Chocolate ao Leite', 1, 'kg', 'Harald', 32.90, 15, 'Chocolate para produção de brigadeiros, trufas e ovos de colher'),
('Chocolate Meio Amargo', 1, 'kg', 'Sicao', 36.50, 10, 'Chocolate usado em receitas com sabor mais intenso'),
('Chocolate Branco', 1, 'kg', 'Harald', 34.90, 12, 'Chocolate branco para trufas, recheios e decoração'),
('Leite Condensado', 395, 'g', 'Moça', 7.99, 40, 'Ingrediente base para brigadeiros e recheios'),
('Creme de Leite', 200, 'g', 'Nestlé', 4.50, 35, 'Usado para ganaches, recheios e coberturas'),
('Granulado de Chocolate', 500, 'g', 'Dori', 12.90, 20, 'Granulado para acabamento de brigadeiros'),
('Manteiga Sem Sal', 200, 'g', 'Aviação', 11.50, 8, 'Usada no preparo de brigadeiros e massas'),
('Morango', 1, 'kg', NULL, 18.00, 6, 'Fruta usada em trufas, recheios e sobremesas'),
('Embalagem para Trufa', 100, 'unidade', 'Cromus', 14.90, 25, 'Embalagens individuais para trufas'),
('Forma para Ovo de Páscoa', 1, 'unidade', 'BWB', 9.90, 10, 'Forma plástica para produção de ovos de chocolate'),
('Colher Descartável', 50, 'unidade', 'PraFesta', 6.90, 18, 'Colheres usadas em ovos de colher e sobremesas'),
('Caixa para Doces', 10, 'unidade', 'Cromus', 22.00, 14, 'Caixas para montagem e entrega de pedidos');

COMMIT;