INSERT INTO user (name, email, password, role)
VALUES
    ('Admin', 'admin@threeboys.com', 'hash1', 'ADMIN'),
    ('Funcionário', 'funcionario@threeboys.com', 'hash2', 'FUNCIONARIO') AS new
    ON DUPLICATE KEY UPDATE
        name = new.name,
        password = new.password,
        role = new.role;