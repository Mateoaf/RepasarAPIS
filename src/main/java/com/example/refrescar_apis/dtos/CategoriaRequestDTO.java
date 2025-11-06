package com.example.refrescar_apis.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para *recibir* la solicitud de crear una nueva categoría.
 */
@Data
@NoArgsConstructor
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres.")
    private String nombre;
}