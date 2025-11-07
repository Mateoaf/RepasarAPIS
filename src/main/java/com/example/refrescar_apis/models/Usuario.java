package com.example.refrescar_apis.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set; // ¡Importante! Usar Set para roles

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Aquí guardaremos el hash de BCrypt

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "usuario_roles", // Nombre de la nueva tabla intermedia
            joinColumns = @JoinColumn(name = "usuario_id"), // Columna que apunta a esta entidad (Usuario)
            inverseJoinColumns = @JoinColumn(name = "rol_id") // Columna que apunta a la otra entidad (Rol)
    )
    private Set<Rol> roles;
}