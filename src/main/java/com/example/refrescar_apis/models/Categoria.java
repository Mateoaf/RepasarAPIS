package com.example.refrescar_apis.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    // --- Definición de la Relación (El lado "Uno") ---

    // mappedBy = "categoria" <-- "Busca el campo 'categoria' en la clase Producto"
    // cascade = CascadeType.ALL <-- Si borro una categoría, borra sus productos.
    // orphanRemoval = true <-- Si quito un producto de esta lista, bórralo.
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // <-- ¡CRÍTICO! Evita bucles infinitos al convertir a JSON
    private List<Producto> productos;
}