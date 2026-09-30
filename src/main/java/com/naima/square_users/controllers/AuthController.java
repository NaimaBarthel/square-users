package com.naima.square_users.controllers;

import com.naima.square_users.controllers.dto.LoginRequestDto;
import com.naima.square_users.controllers.dto.TokenResponseDto;
import com.naima.square_users.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur REST dédié à l'authentification et à la remise de jetons JWT.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "Endpoints d'authentification et émission de tokens")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Injection par constructeur des composants nécessaires.
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Authentifie l'utilisateur via AuthenticationManager et retourne un JWT.
     */
    @PostMapping("/login")
    @Operation(summary = "Authentifier un utilisateur et récupérer un JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie, token retourné"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
        try {
            // 1. Tente d'authentifier les identifiants fournis (utilise PasswordEncoder.matches et UserDetailsService)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

            // 2. Si aucune exception n'a été levée, les identifiants sont corrects : on génère le jeton JWT
            String token = jwtService.generateToken(loginDto.getUsername(), List.of("ROLE_USER"));

            // 3. On renvoie le code 200 OK avec le jeton encapsulé dans le DTO
            return ResponseEntity.ok(new TokenResponseDto(token));

        } catch (BadCredentialsException e) {
            // 4. Si le mot de passe ou l'identifiant est incorrect, Spring Security lève BadCredentialsException -> 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
