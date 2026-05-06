package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.Colaborador;
import com.grupo6.gestao_florestal_api.domain.Role;
import com.grupo6.gestao_florestal_api.domain.User;
import com.grupo6.gestao_florestal_api.domain.enums.FuncaoColaborador;
import com.grupo6.gestao_florestal_api.dto.ColaboradorRequestDTO;
import com.grupo6.gestao_florestal_api.dto.ColaboradorResponseDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.exception.EntityNotFoundException;
import com.grupo6.gestao_florestal_api.repository.ColaboradorRepository;
import com.grupo6.gestao_florestal_api.repository.RoleRepository;
import com.grupo6.gestao_florestal_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ColaboradorResponseDTO criar(ColaboradorRequestDTO dto) {
        if (colaboradorRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF ja cadastrado: " + dto.cpf());
        }
        if (colaboradorRepository.existsByMatricula(dto.matricula())) {
            throw new BusinessException("Matricula ja cadastrada: " + dto.matricula());
        }

        User user = null;
        if (Boolean.TRUE.equals(dto.criarAcesso())) {
            if (dto.senhaAcesso() == null || dto.senhaAcesso().isBlank()) {
                throw new BusinessException("Senha e obrigatoria ao criar acesso");
            }
            if (dto.emailAcesso() == null || dto.emailAcesso().isBlank()) {
                throw new BusinessException("Email de acesso e obrigatorio ao criar acesso");
            }
            if (userRepository.findByUsername(dto.cpf()).isPresent()) {
                throw new BusinessException("Ja existe um usuario com este CPF como username");
            }
            if (userRepository.existsByEmail(dto.emailAcesso())) {
                throw new BusinessException("Ja existe um usuario com este email");
            }

            Role roleColaborador = roleRepository.findByName("ROLE_COLABORADOR")
                    .orElseThrow(() -> new EntityNotFoundException("Role ROLE_COLABORADOR nao encontrada"));

            user = new User();
            user.setUsername(dto.cpf());
            user.setEmail(dto.emailAcesso());
            user.setPassword(passwordEncoder.encode(dto.senhaAcesso()));
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setRoles(Set.of(roleColaborador));
            user = userRepository.save(user);
        }

        Colaborador colaborador = Colaborador.builder()
                .nomeCompleto(dto.nomeCompleto())
                .cpf(dto.cpf())
                .matricula(dto.matricula())
                .funcao(dto.funcao())
                .areaAtuacao(dto.areaAtuacao())
                .dataAdmissao(dto.dataAdmissao())
                .qualificacoes(dto.qualificacoes())
                .certificacoes(dto.certificacoes())
                .contatoEmergenciaNome(dto.contatoEmergenciaNome())
                .contatoEmergenciaTel(dto.contatoEmergenciaTel())
                .ativo(true)
                .user(user)
                .build();

        return ColaboradorResponseDTO.from(colaboradorRepository.save(colaborador));
    }

    @Transactional(readOnly = true)
    public Page<ColaboradorResponseDTO> listar(FuncaoColaborador funcao, String areaAtuacao, Boolean ativo, Pageable pageable) {
        return colaboradorRepository
                .findWithFilters(funcao, areaAtuacao, ativo, pageable)
                .map(ColaboradorResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public List<ColaboradorResponseDTO> listarAtivos() {
        return colaboradorRepository.findByAtivoTrueOrderByNomeCompletoAsc()
                .stream()
                .map(ColaboradorResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ColaboradorResponseDTO buscarPorId(UUID id) {
        return ColaboradorResponseDTO.from(buscarEntidade(id));
    }

    @Transactional
    public ColaboradorResponseDTO atualizar(UUID id, ColaboradorRequestDTO dto) {
        Colaborador colaborador = buscarEntidade(id);

        if (!colaborador.getCpf().equals(dto.cpf()) && colaboradorRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF ja cadastrado: " + dto.cpf());
        }
        if (!colaborador.getMatricula().equals(dto.matricula()) && colaboradorRepository.existsByMatricula(dto.matricula())) {
            throw new BusinessException("Matricula ja cadastrada: " + dto.matricula());
        }

        colaborador.setNomeCompleto(dto.nomeCompleto());
        colaborador.setCpf(dto.cpf());
        colaborador.setMatricula(dto.matricula());
        colaborador.setFuncao(dto.funcao());
        colaborador.setAreaAtuacao(dto.areaAtuacao());
        colaborador.setDataAdmissao(dto.dataAdmissao());
        colaborador.setQualificacoes(dto.qualificacoes());
        colaborador.setCertificacoes(dto.certificacoes());
        colaborador.setContatoEmergenciaNome(dto.contatoEmergenciaNome());
        colaborador.setContatoEmergenciaTel(dto.contatoEmergenciaTel());

        return ColaboradorResponseDTO.from(colaboradorRepository.save(colaborador));
    }

    @Transactional
    public ColaboradorResponseDTO alterarStatus(UUID id, boolean ativo) {
        Colaborador colaborador = buscarEntidade(id);
        colaborador.setAtivo(ativo);

        if (!ativo && colaborador.getUser() != null) {
            User user = colaborador.getUser();
            user.setActive(false);
            userRepository.save(user);
        }

        return ColaboradorResponseDTO.from(colaboradorRepository.save(colaborador));
    }

    private Colaborador buscarEntidade(UUID id) {
        return colaboradorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Colaborador nao encontrado: " + id));
    }
}
