DELIMITER $$

CREATE TRIGGER item_pedido_after_insert
AFTER INSERT ON item_pedido
FOR EACH ROW
BEGIN
    UPDATE pedido
    SET valor_total = (
        SELECT COALESCE(SUM(quantidade * preco_unitario), 0)
        FROM item_pedido
        WHERE pedido_id = NEW.pedido_id
    )
    WHERE id = NEW.pedido_id;
END$$

CREATE TRIGGER item_pedido_after_update
AFTER UPDATE ON item_pedido
FOR EACH ROW
BEGIN
    IF OLD.pedido_id = NEW.pedido_id THEN
        UPDATE pedido
        SET valor_total = (
            SELECT COALESCE(SUM(quantidade * preco_unitario), 0)
            FROM item_pedido
            WHERE pedido_id = NEW.pedido_id
        )
        WHERE id = NEW.pedido_id;
    ELSE
        UPDATE pedido
        SET valor_total = (
            SELECT COALESCE(SUM(quantidade * preco_unitario), 0)
            FROM item_pedido
            WHERE pedido_id = OLD.pedido_id
        )
        WHERE id = OLD.pedido_id;

        UPDATE pedido
        SET valor_total = (
            SELECT COALESCE(SUM(quantidade * preco_unitario), 0)
            FROM item_pedido
            WHERE pedido_id = NEW.pedido_id
        )
        WHERE id = NEW.pedido_id;
    END IF;
END$$

CREATE TRIGGER item_pedido_after_delete
AFTER DELETE ON item_pedido
FOR EACH ROW
BEGIN
    UPDATE pedido
    SET valor_total = (
        SELECT COALESCE(SUM(quantidade * preco_unitario), 0)
        FROM item_pedido
        WHERE pedido_id = OLD.pedido_id
    )
    WHERE id = OLD.pedido_id;
END$$

DELIMITER ;