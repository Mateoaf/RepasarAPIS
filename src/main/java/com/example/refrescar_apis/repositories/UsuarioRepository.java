package com.example.refrescar_apis.repositories;

import com.example.refrescar_apis.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // ¡Este es el método clave que usará Spring Security!
    // Spring Data JPA lo implementa solo por el nombre.
    Optional<Usuario> findByUsername(String username);

    // Para comprobar si ya existe al registrarse
    Boolean existsByUsername(String username);
}