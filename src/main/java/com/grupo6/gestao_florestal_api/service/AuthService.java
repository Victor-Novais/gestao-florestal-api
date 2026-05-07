package com.grupo6.gestao_florestal_api.service;

import com.grupo6.gestao_florestal_api.domain.Role;
import com.grupo6.gestao_florestal_api.domain.User;
import com.grupo6.gestao_florestal_api.dto.AuthMeResponseDTO;
import com.grupo6.gestao_florestal_api.dto.LoginRequestDTO;
import com.grupo6.gestao_florestal_api.dto.LoginResponseDTO;
import com.grupo6.gestao_florestal_api.dto.RefreshRequestDTO;
import com.grupo6.gestao_florestal_api.dto.RefreshResponseDTO;
import com.grupo6.gestao_florestal_api.dto.RegisterRequestDTO;
import com.grupo6.gestao_florestal_api.exception.BusinessException;
import com.grupo6.gestao_florestal_api.repository.RoleRepository;
import com.grupo6.gestao_florestal_api.repository.UserRepository;
import com.grupo6.gestao_florestal_api.security.CustomUserDetailsService;
import com.grupo6.gestao_florestal_api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = null;
        AuthenticationException lastException = null;

        for (String candidateLogin : buildLoginCandidates(request.login())) {
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(candidateLogin, request.password())
                );
                break;
            } catch (AuthenticationException ex) {
                lastException = ex;
            }
        }

        if (authentication == null && lastException != null) {
            throw lastException;
        }

        User user = findByUsername(authentication.getName());
        List<String> roles = extractRoles(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                jwtTokenProvider.getExpiration(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }

    public AuthMeResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username ja cadastrado");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email ja cadastrado");
        }

        Role defaultRole = roleRepository.findByName("ROLE_COLABORADOR")
                .orElseThrow(() -> new BusinessException("Perfil padrao ROLE_COLABORADOR nao encontrado"));

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRoles(Set.of(defaultRole));

        User savedUser = userRepository.save(user);
        return toAuthMeResponse(savedUser);
    }

    public RefreshResponseDTO refresh(RefreshRequestDTO request) {
        if (!jwtTokenProvider.validateToken(request.refreshToken())) {
            throw new BusinessException("RefreshToken invalido ou expirado");
        }

        String username = jwtTokenProvider.getUsernameFromToken(request.refreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!userDetails.isEnabled()) {
            throw new BusinessException("Usuario inativo");
        }

        String newAccessToken = jwtTokenProvider.generateTokenFromUsername(username);
        return new RefreshResponseDTO(newAccessToken, jwtTokenProvider.getExpiration());
    }

    public AuthMeResponseDTO getAuthenticatedUser(String username) {
        return toAuthMeResponse(findByUsername(username));
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario nao encontrado"));
    }

    private List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    private List<String> buildLoginCandidates(String rawLogin) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        String login = rawLogin != null ? rawLogin.trim() : "";
        if (!login.isBlank()) {
            candidates.add(login);
        }

        String digits = login.replaceAll("\\D", "");
        if (digits.length() == 11) {
            candidates.add(digits);
            String maskedCpf = digits.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
            candidates.add(maskedCpf);
        }

        return candidates.stream().toList();
    }

    private AuthMeResponseDTO toAuthMeResponse(User user) {
        return new AuthMeResponseDTO(
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }
}
