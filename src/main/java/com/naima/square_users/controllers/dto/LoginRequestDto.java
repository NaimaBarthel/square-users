package com.naima.square_users.controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Identifiants transmis pour la connexion")
public class LoginRequestDto {

        @Schema(description = "Nom d'utilisateur", example = "alice")
        @NotBlank(message = "Le nom d'utilisateur ne doit pas être vide")
        @JsonProperty("username")
        private String username;

        @Schema(description = "Mot de passe en clair", example = "monSuperMotDePasse123")
        @NotBlank(message = "Le mot de passe ne doit pas être vide")
        @JsonProperty("password")
        private String password;

        // Constructeur sans argument INDISPENSABLE pour la désérialisation Jackson
        public LoginRequestDto() {
        }

        public LoginRequestDto(@JsonProperty("username") String username,
                               @JsonProperty("password") String password) {
                this.username = username;
                this.password = password;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }
}