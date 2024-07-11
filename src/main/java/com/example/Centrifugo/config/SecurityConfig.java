package com.example.Centrifugo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String CONTEXT_PATH = "api/v1/students" ;
    private static final String[] SWAGGER_ENDPOINTS = {

            "/", HttpMethod.GET.name(),
            "/actuator/**",
            CONTEXT_PATH + "/swagger-ui/**",
            CONTEXT_PATH + "/configuration/**",
            CONTEXT_PATH + "/swagger-resources/**",
            CONTEXT_PATH + "/swagger-ui.html/**",
            CONTEXT_PATH + "/api-docs/**",
            CONTEXT_PATH + "/webjars/**",
            CONTEXT_PATH +  "/assets/**",
            CONTEXT_PATH +  "/static/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                        .anyRequest().permitAll())
                .build();
    }


//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(Customizer.withDefaults())
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests((auth) -> auth
//                        .requestMatchers(SWAGGER_ENDPOINTS)
//                        .permitAll()
//                        .anyRequest()
//                        .permitAll()
////                        .authenticated())
////                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
////                .addFilterAfter(createPolicyEnforcerFilter(), BearerTokenAuthenticationFilter.class)
//                .build();
//    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(List.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.applyPermitDefaultValues();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

