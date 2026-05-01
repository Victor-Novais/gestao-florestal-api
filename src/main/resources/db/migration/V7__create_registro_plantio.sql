CREATE TABLE registro_plantio (
    id                  UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    data_hora           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    area_florestal_id   UUID          NOT NULL,
    especie_vegetal_id  UUID          NOT NULL,
    colaborador_id      UUID          NOT NULL,
    quantidade_mudas    INTEGER       NOT NULL,
    latitude_talhao     NUMERIC(10,6),
    longitude_talhao    NUMERIC(10,6),
    temperatura         NUMERIC(5,2),
    umidade             NUMERIC(5,2),
    chuva               BOOLEAN       NOT NULL DEFAULT FALSE,
    metodo_plantio      VARCHAR(50)   NOT NULL,
    observacoes         TEXT,
    num_protocolo       VARCHAR(50)   NOT NULL UNIQUE,
    CONSTRAINT fk_plantio_area
        FOREIGN KEY (area_florestal_id) REFERENCES area_florestal(id),
    CONSTRAINT fk_plantio_especie
        FOREIGN KEY (especie_vegetal_id) REFERENCES especie_vegetal(id),
    CONSTRAINT fk_plantio_colaborador
        FOREIGN KEY (colaborador_id) REFERENCES colaboradores(id)
);
