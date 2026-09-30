package com.naima.square_users.dao.repositories;

import com.naima.square_users.dao.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Interface d'accès aux données pour l'entité {@link UserEntity}.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    /**
     * Recherche un utilisateur à partir de son nom unique.
     * Spring Data JPA génère automatiquement la requête SQL correspondante.
     *
     * @param username Le pseudo recherché.
     * @return Un {@link Optional} contenant l'entité si trouvée, ou vide sinon.
     */
    Optional<UserEntity> findByUsername(String username);

}
