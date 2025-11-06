package com.example.refrescar_apis.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoResponseDTO {

    // Esta es la "vista" pública de nuestro producto
    private Long id;
    private String nombre;
    private double precio;

    // Si quisiéramos añadir un campo "precioConIVA", ¡lo pondríamos aquí!
    // private double precioConIVA;
    private CategoriaDTO categoria;
}