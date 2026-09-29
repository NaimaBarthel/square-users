
# API Users

API REST développée avec Spring Boot permettant de gérer les utilisateurs de l'application de jeux [square-games](https://github.com/NaimaBarthel/square-games).  
Cette API fonctionne comme un service indépendant en complément de l'API Games. Elle permet notamment à l'API Games de vérifier qu'un utilisateur existe (via son identifiant) avant de lui permettre de créer une partie ou d'effectuer des coups sur le plateau.

---

## 🛠️ Technologies

* **Java 21**
* **Spring Boot 3.x**
* **Spring Web MVC**
* **Spring Data JPA**
* **H2 Database** (persistance locale ou en mémoire)
* **springdoc-openapi / Swagger UI** (v2.5.0)
* **Maven** (avec Maven Wrapper)

---

## 📁 Architecture du projet

```text
square-users
│
├── config
│   └── OpenApiConfig
│
├── controllers
│   ├── UserController
│   └── dto
│       └── UserCreationDto
│
├── dao
│   ├── entities
│   │   └── UserEntity
│   └── repositories
│       └── UserRepository (JpaRepository)
│
├── services
│   ├── UserService
│   └── UserServiceImpl
│
└── resources
    ├── application.properties
    └── application-local.properties

```

Le projet respecte une séparation stricte des responsabilités :

* **Controller** : réceptionne les requêtes HTTP, valide les formats et gère les codes de retour HTTP.
* **Service** : porte la logique métier (règles de validation, interactions).
* **DAO / Persistence** : interagit avec la base de données H2 via Spring Data JPA.

---

## ⚙️ Prérequis

Avant de lancer le projet, assurez-vous d'avoir installé :

* **JDK 21** ou supérieur
* **Git**
* Un terminal configuré avec accès Internet pour la résolution initiale des dépendances Maven.

---

## 🗄️ Base de données

L'application s'appuie sur une base de données relationnelle **H2**.

La configuration standard se trouve dans `src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:h2:mem:users_db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

```

*(Si vous optez pour une persistance sur disque entre les redémarrages : `spring.datasource.url=jdbc:h2:file:./data/users_db`).*

### Console web H2

Lorsque l'application tourne :

* **URL :** `http://localhost:8081/h2-console`
* **JDBC URL :** `jdbc:h2:mem:users_db` (ou `jdbc:h2:file:./data/users_db`)
* **User :** `sa`
* **Password :** *(vide)*

---

## 🔧 Configuration

L'API Users est configurée sur le port **8081** pour ne pas entrer en conflit avec l'API Games (qui écoute sur le port **8080**).

Dans `src/main/resources/application.properties` :

```properties
server.port=8081
spring.application.name=square-users

```

---

## ▶️ Lancer l'application

### 1. Cloner le repository

```bash
git clone [https://github.com/NaimaBarthel/square-users.git](https://github.com/NaimaBarthel/square-users.git)
cd square-users

```

### 2. Démarrer avec le Maven Wrapper

**Sous Linux / macOS :**

```bash
./mvnw spring-boot:run

```

**Sous Windows :**

```cmd
.\mvnw.cmd spring-boot:run

```

L'application démarre et écoute sur :

```text
http://localhost:8081

```

---

## 👤 Gestion des utilisateurs (Endpoints REST)

| Méthode | Endpoint | Description | Codes HTTP retournés |
| --- | --- | --- | --- |
| `POST` | `/users` | Enregistre un nouvel utilisateur | `201 Created`, `400 Bad Request` |
| `GET` | `/users/{id}` | Récupère un utilisateur par son identifiant UUID | `200 OK`, `404 Not Found` |
| `DELETE` | `/users/{id}` | Supprime un compte utilisateur | `204 No Content`, `404 Not Found` |
| `GET` | `/users/{id}/valid` | Vérifie l'existence de l'utilisateur (inter-services) | `200 OK`, `404 Not Found` |

---

### Créer un utilisateur

`POST http://localhost:8081/users`

**Content-Type :** `application/json`

**Exemple de corps :**

```json
{
  "username": "Alice",
  "email": "alice@example.com"
}

```

**Réponse (`201 Created`) :**

```json
{
  "id": "e522d61b-8f0c-4556-976a-a3837b9ccb74",
  "username": "Alice",
  "email": "alice@example.com"
}

```

---

### Récupérer un utilisateur

`GET http://localhost:8081/users/{id}`

**Exemple :** `GET http://localhost:8081/users/e522d61b-8f0c-4556-976a-a3837b9ccb74`

**Réponse (`200 OK`) :**

```json
{
  "id": "e522d61b-8f0c-4556-976a-a3837b9ccb74",
  "username": "Alice",
  "email": "alice@example.com"
}

```

*Si l'identifiant n'existe pas : `404 Not Found`.*

---

### Supprimer un utilisateur

`DELETE http://localhost:8081/users/{id}`

**Exemple :** `DELETE http://localhost:8081/users/e522d61b-8f0c-4556-976a-a3837b9ccb74`

**Réponse :** `204 No Content`

---

### Vérifier la validité / existence d'un utilisateur

`GET http://localhost:8081/users/{id}/valid`

Endpoint d'authentification inter-services utilisé par `square-games` via `RestClient` :

* **Si l'utilisateur existe :** `HTTP/1.1 200 OK` (corps vide)
* **Si l'utilisateur n'existe pas :** `HTTP/1.1 404 Not Found`

---

## 🔄 Communication avec l'API Games

L'API `square-games` (port `8080`) interroge ce microservice pour contrôler la légitimité des actions de jeu :

```text
Navigateur / Bruno / Postman
            │
            │ HTTP Request (Header: X-UserId: <UUID>)
            ▼
     square-games :8080
            │
            │ GET http://localhost:8081/users/{id}/valid (via RestClient)
            ▼
     square-users :8081
            │
            ▼
       H2 Database

```

* Si `square-users` répond `200 OK`, `square-games` poursuit l'exécution du coup ou la création de partie.
* Si `square-users` répond `404 Not Found`, `square-games` rejette immédiatement l'action avec un code `403 Forbidden`.

> ⚠️ Pour tester les fonctionnalités de bout en bout, les deux applications doivent tourner simultanément (`8080` et `8081`).

---

## 📖 Swagger / OpenAPI

La documentation interactive est générée automatiquement à chaque lancement :

* **Swagger UI :** [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
* **Spécification JSON brute :** [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

L'interface web permet de tester tous les endpoints directement depuis votre navigateur avec le bouton **Try it out**.

---

## 🔐 Sécurité & Évolution

Dans la phase actuelle, l'identité du joueur est transmise simplement par l'en-tête `X-UserId`.

Ce mode de fonctionnement permet de valider le découplage et les requêtes inter-services via `RestClient`.

Une sécurisation renforcée (avec gestion de jetons d'authentification **JWT**, filtres d'autorisation et hachage BCrypt) est prévue pour l'itération suivante.
EOF

```

