# 👥 Square Users Service

Microservice REST de gestion des identités et de validation d'utilisateurs pour la plateforme **Square Games**, développé avec **Spring Boot 3** et **Java 21**.

---

## 📌 Fonctionnalités

- **CRUD Utilisateurs** : Création, consultation, suppression de profils de joueurs.
- **Persistance des données** : Base de données relationnelle en mémoire (H2) via Spring Data JPA.
- **Validation inter-services** : Endpoint ultra-léger `GET /users/{id}/valid` permettant aux services tiers (ex. `square-games`) de vérifier l'existence d'un identifiant sans surcharge de payload.
- **Documentation OpenAPI** : Spécification interactive intégrée via **SpringDoc v2 / Swagger UI**.

---

## 🚀 Démarrage rapide

### Prérequis
- **JDK 21** ou supérieur
- **Maven 3.8+** (ou wrapper `mvnw`)

### Configuration
Par défaut, le microservice s'exécute sur le port **`8081`** afin de cohabiter avec `square-games` (`8080`).

Vérifiez dans `src/main/resources/application.properties` :
```properties
server.port=8081
spring.application.name=square-users

# Configuration H2
spring.datasource.url=jdbc:h2:mem:usersdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true


Lancement
# Compilation et démarrage
mvn clean spring-boot:run

L'application écoute sur : http://localhost:8081.

📖 Documentation de l'API (Swagger UI)

Dès que l'application est démarrée, consultez la documentation interactive :

    Swagger UI : http://localhost:8081/swagger-ui/index.html

    Spécification JSON brute (OpenAPI 3) : http://localhost:8081/v3/api-docs

    Console H2 : http://localhost:8081/h2-console (JDBC URL: jdbc:h2:mem:usersdb, utilisateur: sa)

## 🔌 Récapitulatif des Endpoints

| Méthode | Endpoint | Description | Réponses HTTP |
| :--- | :--- | :--- | :--- |
| `POST` | `/users` | Enregistre un nouvel utilisateur | `201 Created`, `400 Bad Request` |
| `GET` | `/users/{id}` | Récupère le profil d'un utilisateur par son ID | `200 OK`, `404 Not Found` |
| `DELETE` | `/users/{id}` | Supprime définitivement un utilisateur | `204 No Content`, `404 Not Found` |
| `GET` | `/users/{id}/valid` | Vérifie l'existence de l'utilisateur (utilisé par `square-games`) | `200 OK` (vide), `404 Not Found` |