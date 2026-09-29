package com.naima.square_users.controllers;

import com.naima.square_users.controllers.dto.UserCreationDto;
import com.naima.square_users.dao.entities.UserEntity;
import com.naima.square_users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Utilisateurs", description = "Endpoints de gestion et de validation des comptes utilisateurs")
public class UserController {
    private final UserService userService;

    //Injection par constructeur
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //1. créer un utilisateur (renvoie 201 Created)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @ApiResponse(responseCode = "400", description = "Format des données invalide ou email déjà existant")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un utilisateur avec mot de passe haché")
    public UserEntity createUser(@RequestBody UserCreationDto dto){
        return userService.createUser(dto);
    }

    //2. récupérer un utilisateur par son id (renvoie 200 OK ou 404 Not Found)
    @Operation(
            summary = "Obtenir un utilisateur par son identifiant",
            description = "Recherche un compte utilisateur existant à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
                    content = @Content(schema = @Schema(implementation = UserEntity.class))),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé avec cet identifiant")
    })
    @GetMapping("/{id}")

    public ResponseEntity<UserEntity> getUserById(
            @Parameter(description = "Identifiant de l'utilisateur", required = true, example = "fb20f8b4-c32c-43cf-8b4e-0418880ae115")
            @PathVariable("id") String id){
        return userService.getUserById(id)
                .map(ResponseEntity::ok)       // Si présent : emballe dans un 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build());      // Si absent : renvoie un 404 Not Found
    }

    //3. supprimer un utilisateur (204 No Content)

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)

    public void deleteUser(
            @Parameter(description = "Identifiant de l'utilisateur à supprimer", required = true, example = "fb20f8b4-c32c-43cf-8b4e-0418880ae115")
            @PathVariable("id") String id){
        userService.deleteUser(id);
    }

    //4. vérifier la validité de l'utilisateur (200 OK ou 404 Not Found)

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "L'utilisateur existe et est valide (corps de réponse vide)"),
            @ApiResponse(responseCode = "404", description = "Utilisateur inexistant ou invalide")
    })
    @GetMapping("/{id}/valid")

    public ResponseEntity<Void> isUserValid(
            @Parameter(description = "Identifiant de l'utilisateur à vérifier", required = true, example = "fb20f8b4-c32c-43cf-8b4e-0418880ae115")
            @PathVariable("id") String id) {
        if (userService.isUserValid(id)){
            return ResponseEntity.ok().build();   //Si valide : renvoie 200 OK
        }
        else{
            return ResponseEntity.notFound().build();    //Si non valide : renvoie 404 Not Found
        }
    }
}
