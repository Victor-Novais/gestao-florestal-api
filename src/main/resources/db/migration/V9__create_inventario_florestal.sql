CREATE TABLE inventario_florestal (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    numero_parcela      VARCHAR(50) NOT NULL,
    area_florestal_id   UUID NOT NULL,
    data_vistoria       DATE NOT NULL,
    presenca_pragas     BOOLEAN NOT NULL DEFAULT FALSE,
    descricao_pragas    TEXT,
    estado_geral        VARCHAR(20) NOT NULL,
    colaborador_id      UUID NOT NULL,
    criado_em           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_inventario_area
        FOREIGN KEY (area_florestal_id) REFERENCES area_florestal(id),
    CONSTRAINT fk_inventario_colaborador
        FOREIGN KEY (colaborador_id) REFERENCES colaboradores(id),
    CONSTRAINT check_estado_geral
        CHECK (estado_geral IN ('OTIMO', 'BOM', 'REGULAR', 'CRITICO'))
);

CREATE INDEX idx_inventario_area ON inventario_florestal(area_florestal_id);
CREATE INDEX idx_inventario_colaborador ON inventario_florestal(colaborador_id);
CREATE INDEX idx_inventario_parcela ON inventario_florestal(numero_parcela);

CREATE TABLE inventario_especie (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    inventario_id       UUID NOT NULL,
    especie_vegetal_id  UUID NOT NULL,
    quantidade          INTEGER NOT NULL,
    dap_medio           DECIMAL(10,2),
    altura_media        DECIMAL(10,2),

    CONSTRAINT fk_inv_especie_inventario
        FOREIGN KEY (inventario_id) REFERENCES inventario_florestal(id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_especie_especie
        FOREIGN KEY (especie_vegetal_id) REFERENCES especie_vegetal(id)
);

CREATE INDEX idx_inv_especie_inventario ON inventario_especie(inventario_id);
CREATE INDEX idx_inv_especie_especie ON inventario_especie(especie_vegetal_id);