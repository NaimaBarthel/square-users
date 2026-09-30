package com.naima.square_users.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Service utilitaire assurant la manipulation complète des JSON Web Tokens (JWT).
 * <p>
 * Il prend en charge :
 * <ul>
 *     <li>La génération et la signature cryptographique du jeton (HMAC-SHA256).</li>
 *     <li>L'extraction des revendications (claims) telles que le nom d'utilisateur.</li>
 *     <li>La vérification de l'intégrité de la signature et de la validité temporelle.</li>
 * </ul>
 * </p>
 */
@Service
public class JwtService {
    /**
     * Clé secrète encodée en Base64, lue depuis le fichier application.properties.
     * Doit faire au moins 256 bits pour garantir la robustesse de l'algorithme HS256.
     */
    @Value("${jwt.secret}")
    private String secretKey;

/**
 * Durée de validité du jeton en millisecondes (ex. 86400000 ms pour 24 heures).
 */
@Value("${jwt.expiration}")
private long jwtExpiration;

/**
 * Décode la clé secrète configurée en Base64 et instancie la clé HMAC-SHA.
 *
 * @return La clé secrète cryptographique utilisée pour signer et vérifier les tokens.
 */
private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
}

/**
 * Génère un token JWT signé contenant le nom d'utilisateur et ses rôles.
 *
 * @param username Le pseudo de l'utilisateur (inscrit dans le claim standard 'sub').
 * @param roles    La liste des rôles applicatifs accordés (ex. ROLE_USER).
 * @return Le token JWT compacté sous forme de chaîne (header.payload.signature).
 */
public String generateToken(String username, List<String> roles){
    return Jwts.builder()  // Démarre la construction pas à pas du jeton JWT
            .subject(username)   // Définit le sujet ('sub') : l'identifiant du joueur
            .claim("roles", roles)  // Ajoute une donnée personnalisée dans le payload : les rôles
            .issuedAt(new Date(System.currentTimeMillis()))  // Enregistre la date et heure exacte de création ('iat')
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))   // Calcule et fixe la date limite de validité ('exp')
            .signWith(getSigningKey())   // Signe cryptographiquement le jeton avec notre clé secrète HMAC-SHA
            .compact();   // Concatène et encode en Base64 les 3 parties (header.payload.signature)

}

/**
 * Déchiffre et contrôle la signature du token à l'aide de la clé secrète,
 * puis extrait l'ensemble des revendications (claims).
 *
 * @param token Le jeton JWT à analyser.
 * @return L'ensemble des claims contenus dans le corps du token.
 */
private Claims extractAllClaims(String token){
    return Jwts.parser()   // Initialise le configurateur d'analyse (parser) de token JWT
            .verifyWith(getSigningKey())   //Fournit la clé secrète pour vérifier l'authenticité de la signature
            .build()   //Construit l'instance finale et immuable du parser
            .parseSignedClaims(token)   // Décode la chaîne, contrôle la validité cryptographique et lève une exception en cas d'altération
            .getPayload();   // Récupère le corps déchiffré (Claims) contenant l'ensemble des données (sub, roles, exp, etc.)
}

/**
 * Extrait le nom d'utilisateur (sujet / subject) contenu dans le token.
 *
 * @param token Le jeton JWT brut reçu du client.
 * @return Le nom d'utilisateur extrait du payload.
 */

public String extractUsername(String token){
    return extractAllClaims(token).getSubject();
}

/**
 * Vérifie la conformité du token : authenticité de la signature et non-expiration.
 *
 * @param token Le jeton JWT à contrôler.
 * @return {@code true} si la signature est valide et le token non expiré, {@code false} sinon.
 */
public boolean isTokenValid(String token){
    try{
        Claims claims = extractAllClaims(token);
        // Vérifie que la date d'expiration n'est pas antérieure à la date actuelle
        return !claims.getExpiration().before(new Date());
    } catch(Exception e) {
        //En cas d'altération, de clé erronée ou d'expiration dépassée
        return false;
    }
}




}
