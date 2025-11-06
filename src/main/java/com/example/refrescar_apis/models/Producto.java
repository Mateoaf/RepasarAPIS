package com.example.refrescar_apis.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@Entity
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;


    private double precio;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY = No cargues la categoría hasta que la pida
    @JoinColumn(name = "categoria_id", nullable = false) // Nombre de la columna en la BBDD
    private Categoria categoria;

}
