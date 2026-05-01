package com.grupo6.gestao_florestal_api.event;

import com.grupo6.gestao_florestal_api.domain.Ocorrencia;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OcorrenciaAlertaEvent extends ApplicationEvent {

    private final Ocorrencia ocorrencia;

    public OcorrenciaAlertaEvent(Object source, Ocorrencia ocorrencia) {
        super(source);
        this.ocorrencia = ocorrencia;
    }
}