CREATE TABLE ocorrencia (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    numero_protocolo    VARCHAR(20) NOT NULL UNIQUE,
    tipo                VARCHAR(30) NOT NULL,
    area_florestal_id   UUID NOT NULL,
    latitude            DECIMAL(10, 6),
    longitude           DECIMAL(10, 6),
    urgencia            VARCHAR(10) NOT NULL,
    descricao           TEXT,
    data_registro       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    colaborador_id      UUID NOT NULL,
    criado_em           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ocorrencia_area
        FOREIGN KEY (area_florestal_id) REFERENCES area_florestal(id),
    CONSTRAINT fk_ocorrencia_colaborador
        FOREIGN KEY (colaborador_id) REFERENCES colaboradores(id),
    CONSTRAINT check_tipo_ocorrencia
        CHECK (tipo IN ('INCENDIO', 'DESMATAMENTO_ILEGAL', 'EROSAO', 'ESPECIE_INVASORA',
                        'PRAGA_DOENCA', 'ACIDENTE_ANIMAL', 'ACIDENTE_EQUIPE', 'INFRACAO_AMBIENTAL')),
    CONSTRAINT check_urgencia
        CHECK (urgencia IN ('BAIXO', 'MEDIO', 'ALTO', 'CRITICO'))
);


CREATE TABLE ocorrencia_foto (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ocorrencia_id   UUID NOT NULL,
    url_foto        VARCHAR(500) NOT NULL,

    CONSTRAINT fk_ocorrencia_foto
        FOREIGN KEY (ocorrencia_id) REFERENCES ocorrencia(id) ON DELETE CASCADE
);


CREATE INDEX idx_ocorrencia_area ON ocorrencia(area_florestal_id);
CREATE INDEX idx_ocorrencia_colaborador ON ocorrencia(colaborador_id);
CREATE INDEX idx_ocorrencia_tipo ON ocorrencia(tipo);
CREATE INDEX idx_ocorrencia_urgencia ON ocorrencia(urgencia);
CREATE INDEX idx_ocorrencia_data ON ocorrencia(data_registro);
CREATE INDEX idx_ocorrencia_foto_ocorrencia ON ocorrencia_foto(ocorrencia_id);