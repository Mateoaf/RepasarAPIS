package com.example.refrescar_apis.repositories;

import com.example.refrescar_apis.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Spring Data JPA nos da findById(), save(), findAll(), etc.
}