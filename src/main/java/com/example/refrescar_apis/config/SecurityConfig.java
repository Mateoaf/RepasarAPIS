package com.example.refrescar_apis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration // Le dice a Spring que esta es una clase de configuración
@EnableWebSecurity // Activa la seguridad web de Spring
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        // 1. Usamos el codificador que acabamos de crear
        PasswordEncoder encoder = passwordEncoder();

        // 2. Creamos un usuario "admin"
        var admin = User.withUsername("admin")
                .password(encoder.encode("admin123")) // ¡Contraseña codificada!
                .roles("ADMIN") // Le damos el rol "ADMIN"
                .build();

        // 3. Creamos un usuario "user"
        var user = User.withUsername("user")
                .password(encoder.encode("user123"))
                .roles("USER") // Le damos el rol "USER"
                .build();

        // 4. Los guardamos en un gestor de usuarios en memoria
        return new InMemoryUserDetailsManager(admin, user);
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtRequestFilter jwtRequestFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize

                        .requestMatchers(toH2Console()).permitAll()
                        .requestMatchers("/login").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll() // Cualquiera puede VER categorías
                        .requestMatchers(HttpMethod.POST, "/api/categorias").hasRole("ADMIN") // Solo ADMIN puede CREAR categorías
                        .anyRequest().authenticated()
                )

                // Y lo usamos aquí
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}