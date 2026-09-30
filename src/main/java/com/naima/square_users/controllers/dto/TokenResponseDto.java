package com.naima.square_users.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO retourné après authentification réussie, encapsulant le jeton JWT.
 */
@Schema(description = "Réponse contenant le jeton d'accès")
public record TokenResponseDto(
   @Schema(description = "Jeton JWT Bearer généré", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
   String token
) {}
