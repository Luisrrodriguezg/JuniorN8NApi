-- src/main/resources/data.sql
INSERT INTO products (name, type, size, price, stock) VALUES
-- Camiseta Local
('Camiseta Local 2025',        'CAMISETA_LOCAL',       'S',    220000.00, 25),
('Camiseta Local 2025',        'CAMISETA_LOCAL',       'M',    220000.00, 18),
('Camiseta Local 2025',        'CAMISETA_LOCAL',       'L',    220000.00, 30),
('Camiseta Local 2025',        'CAMISETA_LOCAL',       'XL',   220000.00, 4),

-- Camiseta Visitante
('Camiseta Visitante 2025',    'CAMISETA_VISITANTE',   'S',    220000.00, 12),
('Camiseta Visitante 2025',    'CAMISETA_VISITANTE',   'M',    220000.00, 0),
('Camiseta Visitante 2025',    'CAMISETA_VISITANTE',   'L',    220000.00, 22),
('Camiseta Visitante 2025',    'CAMISETA_VISITANTE',   'XL',   220000.00, 9),

-- Camiseta Alternativa
('Camiseta Alternativa 2025',  'CAMISETA_ALTERNATIVA', 'M',    240000.00, 15),
('Camiseta Alternativa 2025',  'CAMISETA_ALTERNATIVA', 'L',    240000.00, 8),

-- Pantaloneta
('Pantaloneta Oficial',        'PANTALONETA',          'M',     85000.00, 40),
('Pantaloneta Oficial',        'PANTALONETA',          'L',     85000.00, 15),
('Pantaloneta Oficial',        'PANTALONETA',          'XL',    85000.00, 0),

-- Medias
('Medias Tiburones',           'MEDIAS',               'M',     35000.00, 60),
('Medias Tiburones',           'MEDIAS',               'L',     35000.00, 45),

-- Gorra (one-size → None)
('Gorra Junior',               'GORRA',                'None',  55000.00, 8),
('Gorra Snapback Tiburón',     'GORRA',                'None',  65000.00, 19),

-- Peluche Tiburón (one-size → None)
('Peluche Tiburón Pequeño',    'PELUCHE_TIBURON',      'None',  45000.00, 30),
('Peluche Tiburón Grande',     'PELUCHE_TIBURON',      'None',  85000.00, 12),

-- Camiseta para perro (it's adorable that you included this)
('Camiseta Perro Junior',      'CAMISETA_PERRO',       'S',     55000.00, 7),
('Camiseta Perro Junior',      'CAMISETA_PERRO',       'M',     55000.00, 11),
('Camiseta Perro Junior',      'CAMISETA_PERRO',       'L',     55000.00, 0);