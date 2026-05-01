CREATE TABLE colaboradores (
    id                        UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome_completo             VARCHAR(255) NOT NULL,
    cpf                       VARCHAR(14)  NOT NULL UNIQUE,
    matricula                 VARCHAR(50)  NOT NULL UNIQUE,
    funcao                    VARCHAR(50)  NOT NULL,
    area_atuacao              VARCHAR(255),
    data_admissao             DATE         NOT NULL,
    qualificacoes             TEXT,
    certificacoes             TEXT,
    contato_emergencia_nome   VARCHAR(255) NOT NULL,
    contato_emergencia_tel    VARCHAR(20)  NOT NULL,
    ativo                     BOOLEAN      NOT NULL DEFAULT TRUE,
    user_id                   UUID,
    CONSTRAINT fk_colaborador_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
