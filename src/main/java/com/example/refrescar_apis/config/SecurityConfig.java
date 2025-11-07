package com.example.refrescar_apis.config;

import com.example.refrescar_apis.models.Rol;
import com.example.refrescar_apis.models.Usuario;
import com.example.refrescar_apis.repositories.RolRepository;
import com.example.refrescar_apis.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Set;


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
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtRequestFilter jwtRequestFilter) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categorias").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Pon aquí el origen de tu frontend (ej. React en puerto 3000)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173", // (Puerto común de Vite)
                "http://localhost:4200/"

        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeceras permitidas (incluida la de Authorization)
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        // Permitir que el frontend envíe credenciales (como cookies o tokens)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicamos esta configuración a TODAS las rutas de nuestra API ("/**")
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
        @Bean
        public CommandLineRunner initDatabase(RolRepository rolRepository,
                                              UsuarioRepository usuarioRepository,
                                              PasswordEncoder passwordEncoder,
                                              @Value("${DEFAULT_ADMIN_USER}") String adminUser,
                                              @Value("${DEFAULT_ADMIN_PASS}") String adminPass,
                                              @Value("${DEFAULT_USER_USER}") String regularUsername,
                                              @Value("${DEFAULT_USER_PASS}") String regularPassword) {
            return args -> {
                // --- Crear Roles si no existen ---
                Rol adminRol = rolRepository.findByNombre("ROLE_ADMIN").orElseGet(() -> {
                    Rol newRol = Rol.builder().nombre("ROLE_ADMIN").build();
                    return rolRepository.save(newRol);
                });

                Rol userRol = rolRepository.findByNombre("ROLE_USER").orElseGet(() -> {
                    Rol newRol = Rol.builder().nombre("ROLE_USER").build();
                    return rolRepository.save(newRol);
                });

                // --- Crear Usuario Admin si no existe ---
                if (!usuarioRepository.existsByUsername(adminUser)) {
                    Usuario admin = Usuario.builder()
                            .username(adminUser)
                            .password(passwordEncoder.encode(adminPass)) // ¡Usamos el encoder!
                            .roles(Set.of(adminRol, userRol)) // El admin tiene AMBOS roles
                            .build();
                    usuarioRepository.save(admin);
                }

                // --- (Opcional) Crear Usuario User si no existe ---
                if (!usuarioRepository.existsByUsername(regularUsername)) {
                    Usuario regularUser = Usuario.builder()
                            .username(regularUsername)
                            .password(passwordEncoder.encode(regularPassword))
                            .roles(Set.of(userRol)) // El user solo tiene rol USER
                            .build();
                    usuarioRepository.save(regularUser);
                }
            };
        }
    }
