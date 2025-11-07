package com.example.refrescar_apis.service;

import com.example.refrescar_apis.models.Rol;
import com.example.refrescar_apis.models.Usuario;
import com.example.refrescar_apis.repositories.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

// 1. Le decimos a Spring que este es un Bean de Servicio
@Service
// 2. Implementamos la interfaz que Spring Security entiende
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // 3. Inyectamos nuestro repositorio de usuarios
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // 4. Este es el ÚNICO método que Spring Security nos obliga a implementar
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 5. Buscamos al usuario en nuestra BBDD
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // 6. Convertimos nuestros Roles (de la BBDD) a "Authorities" (de Spring)
        Set<GrantedAuthority> authorities = usuario
                .getRoles()
                .stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toSet());

        // 7. Devolvemos el objeto "UserDetails" que Spring Security espera
        return new User(
                usuario.getUsername(),
                usuario.getPassword(), // La contraseña ya hasheada de la BBDD
                authorities
        );
    }
}