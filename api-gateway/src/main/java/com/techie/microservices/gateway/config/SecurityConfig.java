package com.techie.microservices.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration // San Ca Spring n’analysera même pas la classe, donc il ne saura pas que ton bean existe.
public class SecurityConfig {

    private final String[] freeResourceUrls = {"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**", "/api-docs/**", "/aggregate/**", "/actuator/prometheus"};

    @Bean //Elle ne fait pas partie du contexte Spring. Donc a configuration de sécurité ne serait pas appliquée.
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated()) // Toutes les requêtes HTTP doivent être authentifiées.
                        //.requestMatchers(freeResourceUrls).permitAll() // Alors certaines URLs auraient été libres d’accès (pas besoin d’être connecté
                        .oauth2ResourceServer(auth -> auth.jwt(Customizer.withDefaults()))
                        //c’est-à-dire qu’elle protège des ressources et valide les tokens JWT
                        //.cors(cors -> cors.configurationSource(corsConfigurationSource()) //autoriser des appels d’un frontend React/Vue
                        .build();

                        //Spring Security va :
                        //Intercepter toutes les requêtes,
                        //Chercher un en-tête Authorization: Bearer <token>,
                        //Vérifier le token JWT (signature, audience, etc.),
                        //Et authentifier l’utilisateur en conséquence.

                        //SecurityFilterChain
                        //Objet clé contenant les règles de sécurité
                        //Sans lui, Spring Security n’a pas de configuration
    }
}
