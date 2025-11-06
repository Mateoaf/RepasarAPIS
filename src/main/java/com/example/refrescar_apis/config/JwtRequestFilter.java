package com.example.refrescar_apis.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.refrescar_apis.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtRequestFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener la cabecera "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // 2. Comprobar si es un token Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // No es token, sigue adelante
            return;
        }

        // 3. Extraer el token
        final String token = authHeader.substring(7); // Quita "Bearer "

        try {
            // 4. Validar el token
            String username = jwtService.validateTokenAndGetUser(token);

            // 5. Si es válido y no hay nadie logueado en el contexto...
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // ...cargamos los detalles del usuario (de nuestro InMemory BBDD)
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // ...creamos la "sesión" de Spring
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No usamos credenciales (contraseña)
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ...y guardamos al usuario en el Contexto de Seguridad
                // ¡Esto es "loguear" al usuario para esta petición!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (JWTVerificationException e) {
            // El token no es válido (firmar mal, caducado, etc.)
            // No hacemos nada, el SecurityContext queda nulo,
            // y Spring Security denegará el acceso más adelante.
        }

        // 6. Pase lo que pase, continuamos con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}
