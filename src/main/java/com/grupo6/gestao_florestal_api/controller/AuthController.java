package com.grupo6.gestao_florestal_api.controller;

import com.grupo6.gestao_florestal_api.dto.AuthMeResponseDTO;
import com.grupo6.gestao_florestal_api.dto.LoginRequestDTO;
import com.grupo6.gestao_florestal_api.dto.LoginResponseDTO;
import com.grupo6.gestao_florestal_api.dto.RefreshRequestDTO;
import com.grupo6.gestao_florestal_api.dto.RefreshResponseDTO;
import com.grupo6.gestao_florestal_api.dto.RegisterRequestDTO;
import com.grupo6.gestao_florestal_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacao", description = "Endpoints de autenticacao e renovacao de token")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais invalidas")
    })
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Cadastrar usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "422", description = "Username ou email ja cadastrados")
    })
    public ResponseEntity<AuthMeResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Retornar usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario autenticado retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Nao autenticado")
    })
    public ResponseEntity<AuthMeResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getAuthenticatedUser(authentication.getName()));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acesso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Refresh token invalido")
    })
    public ResponseEntity<RefreshResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
