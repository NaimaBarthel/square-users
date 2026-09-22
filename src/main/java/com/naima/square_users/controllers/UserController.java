package com.naima.square_users.controllers;

import com.naima.square_users.controllers.dto.UserCreationDto;
import com.naima.square_users.dao.entities.UserEntity;
import com.naima.square_users.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    //Injection par constructeur
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //1. créer un utilisateur (renvoie 201 Created)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserEntity createUser(@RequestBody UserCreationDto dto){
        return userService.createUser(dto);
    }

    //2. récupérer un utilisateur par son id (renvoie 200 OK ou 404 Not Found)
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable("id") String id){
        return userService.getUserById(id)
                .map(ResponseEntity::ok)       // Si présent : emballe dans un 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build());      // Si absent : renvoie un 404 Not Found
    }

    //3. supprimer un utilisateur (204 No Content)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") String id){
        userService.deleteUser(id);
    }

    //4. vérifier la validité de l'utilisateur (200 OK ou 404 Not Found)
    @GetMapping("/{id}/valid")
    public ResponseEntity<Void> isUserValid(@PathVariable("id") String id) {
        if (userService.isUserValid(id)){
            return ResponseEntity.ok().build();   //Si valide : renvoie 200 OK
        }
        else{
            return ResponseEntity.notFound().build();    //Si non valide : renvoie 404 Not Found
        }
    }
}
