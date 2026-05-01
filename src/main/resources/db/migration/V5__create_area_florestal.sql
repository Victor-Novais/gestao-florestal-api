CREATE TABLE IF NOT EXISTS area_florestal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    identificador_unico VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    latitude DECIMAL(10,6) NOT NULL,
    longitude DECIMAL(10,6) NOT NULL,
    municipio VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    tamanho_hectares DECIMAL(12,2) NOT NULL,
    tipo_floresta VARCHAR(20) NOT NULL,
    bioma_predominante VARCHAR(30) NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_inativacao TIMESTAMP,

    CONSTRAINT check_tipo_floresta CHECK (tipo_floresta IN ('NATIVA', 'PLANTADA', 'MISTA')),
    CONSTRAINT check_bioma CHECK (bioma_predominante IN ('AMAZONIA', 'CERRADO', 'MATA_ATLANTICA', 'CAATINGA', 'PAMPA', 'PANTANAL')),
    CONSTRAINT check_status CHECK (status IN ('ATIVA', 'EM_RECUPERACAO', 'EMBARGADA', 'RESERVADA'))
    );

CREATE INDEX idx_area_florestal_bioma ON area_florestal(bioma_predominante);
CREATE INDEX idx_area_florestal_status ON area_florestal(status);
CREATE INDEX idx_area_florestal_tipo ON area_florestal(tipo_floresta);
CREATE INDEX idx_area_florestal_ativo ON area_florestal(ativo);

