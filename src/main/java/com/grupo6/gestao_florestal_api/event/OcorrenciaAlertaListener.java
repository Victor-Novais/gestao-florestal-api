package com.grupo6.gestao_florestal_api.event;

import com.grupo6.gestao_florestal_api.domain.Ocorrencia;
import com.grupo6.gestao_florestal_api.domain.enums.UrgenciaOcorrencia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OcorrenciaAlertaListener {

    @Async
    @EventListener
    public void handleOcorrenciaAlerta(OcorrenciaAlertaEvent event) {
        Ocorrencia ocorrencia = event.getOcorrencia();
        
        String nivelAlerta = ocorrencia.getUrgencia() == UrgenciaOcorrencia.CRITICO 
                ? " CRÍTICO" 
                : " ALTO";
        
       
        log.warn("Protocolo: {}", ocorrencia.getNumeroProtocolo());
        log.warn("Tipo: {}", ocorrencia.getTipo());
        log.warn("Urgência: {}", ocorrencia.getUrgencia());
        log.warn("Descrição: {}", ocorrencia.getDescricao());
        log.warn("Localização: {}, {}", ocorrencia.getLatitude(), ocorrencia.getLongitude());
        log.warn("Área Florestal: {}", ocorrencia.getAreaFlorestal().getNome());
        log.warn("Colaborador: {}", ocorrencia.getColaborador().getNomeCompleto());
        log.warn("Data/Hora: {}", ocorrencia.getDataRegistro());
        log.warn("═══════════════════════════════════════════════════════════════");
        
    
    }
}