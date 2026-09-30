package com.naima.square_users.config;

import com.naima.square_users.dao.repositories.UserRepository;
import org.h2.server.web.JakartaWebServlet; // ou org.h2.server.web.WebServlet selon la version
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Injection du repository pour interroger la base de données
    private final UserRepository userRepository;

    /**
     * Constructeur de la classe de configuration.
     * Spring détecte automatiquement ce constructeur et lui donne une instance de UserRepository.
     *
     * @param userRepository le composant d'accès à la table des utilisateurs
     */
    public SecurityConfig(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    /**
     * Service chargé de retrouver l'utilisateur en base et de l'adapter
     * au format attendu par Spring Security (UserDetails).
     */
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.emptyList()
                ))
                .orElseThrow( () -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
    }

    /**
     * Configure le fournisseur d'authentification par base de données.
     * <p>
     * Il assemble deux éléments :
     * 1. Où trouver l'utilisateur ? (via notre userDetailsService)
     * 2. Comment vérifier son mot de passe ? (via notre passwordEncoder BCrypt)
     * </p>
     *
     * @return le composant de vérification configuré
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        // On instancie le fournisseur standard de Spring basé sur une base de données
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());

        // 2. On configure l'encodeur de mot de passe via son setter (qui existe toujours)
        authProvider.setPasswordEncoder(passwordEncoder());

        //On retourne l'ouvrier prêt à travailler
        return authProvider;
    }

    /**
     * Expose le gestionnaire d'authentification principal de Spring Security.
     * <p>
     * Ce composant sera injecté dans notre futur AuthController
     * pour valider les requêtes POST /auth/login.
     * </p>
     *
     * @param config la configuration d'authentification globale fournie par Spring
     * @return l'instance de l'AuthenticationManager
     * @throws Exception en cas d'erreur de récupération par le framework
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // On demande à Spring de nous donner son gestionnaire par défaut
        return config.getAuthenticationManager();
    }


    /**
     * Enregistrement forcé de la servlet de la console H2
     */
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2ConsoleServletRegistration() {
        ServletRegistrationBean<JakartaWebServlet> registration =
                new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
        registration.setName("H2Console");
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**", "/h2-console", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}