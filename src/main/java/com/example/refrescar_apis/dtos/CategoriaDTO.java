package com.example.refrescar_apis.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simple para *devolver* información de la Categoría.
 */
@Data
@NoArgsConstructor
public class CategoriaDTO {
    private Long id;
    private String nombre;
}