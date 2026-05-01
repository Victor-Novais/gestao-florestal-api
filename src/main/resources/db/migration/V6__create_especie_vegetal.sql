CREATE TABLE IF NOT EXISTS especie_vegetal (
                                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_cientifico VARCHAR(255) NOT NULL UNIQUE,
    nome_popular VARCHAR(255),
    familia_botanica VARCHAR(255),
    porte VARCHAR(20) NOT NULL,
    status_conservacao VARCHAR(30) NOT NULL,
    ciclo_vida_anos INTEGER,
    exigencias_climaticas TEXT,
    exigencias_solo TEXT,
    nativa BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_porte CHECK (porte IN ('ARBOREO', 'ARBUSTIVO', 'HERBACEO')),
    CONSTRAINT check_status_conservacao CHECK (status_conservacao IN ('AMEACADA', 'VULNERAVEL', 'POUCO_PREOCUPANTE'))
    );

CREATE INDEX idx_especie_vegetal_nome_cientifico ON especie_vegetal(nome_cientifico);
CREATE INDEX idx_especie_vegetal_status_conservacao ON especie_vegetal(status_conservacao);
CREATE INDEX idx_especie_vegetal_porte ON especie_vegetal(porte);
CREATE INDEX idx_especie_vegetal_nativa ON especie_vegetal(nativa);
CREATE INDEX idx_especie_vegetal_ativo ON especie_vegetal(ativo);
