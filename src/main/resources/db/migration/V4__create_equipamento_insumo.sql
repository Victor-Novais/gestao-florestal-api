CREATE TABLE equipamento_insumo (
    id                    UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    codigo_patrimonial    VARCHAR(100)  NOT NULL UNIQUE,
    descricao             VARCHAR(255)  NOT NULL,
    categoria             VARCHAR(50)   NOT NULL,
    quantidade            NUMERIC(10,2) NOT NULL DEFAULT 0,
    unidade_medida        VARCHAR(50)   NOT NULL,
    localizacao_atual     VARCHAR(255),
    data_aquisicao        DATE          NOT NULL,
    vida_util_estimada    INTEGER       NOT NULL,
    estoque_minimo        NUMERIC(10,2) NOT NULL DEFAULT 0,
    ativo                 BOOLEAN       NOT NULL DEFAULT TRUE,
    responsavel_id        UUID,
    CONSTRAINT fk_equipamento_responsavel
        FOREIGN KEY (responsavel_id) REFERENCES colaboradores(id) ON DELETE SET NULL
);
