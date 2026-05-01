CREATE TABLE meta_plantio (
    id                UUID    PRIMARY KEY DEFAULT uuid_generate_v4(),
    area_florestal_id UUID    NOT NULL,
    mes               INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12),
    ano               INTEGER NOT NULL CHECK (ano >= 2000),
    quantidade_meta   INTEGER NOT NULL CHECK (quantidade_meta > 0),
    CONSTRAINT fk_meta_area
        FOREIGN KEY (area_florestal_id) REFERENCES area_florestal(id),
    CONSTRAINT uq_meta_area_mes_ano
        UNIQUE (area_florestal_id, mes, ano)
);
