package com.example.refrescar_apis.repositories;

import com.example.refrescar_apis.models.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método "mágico" para buscar un Rol por su nombre
    Optional<Rol> findByNombre(String nombre);
}