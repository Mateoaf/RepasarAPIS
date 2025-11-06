package com.example.refrescar_apis.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductoRequestDTO {

    // ¡Hemos movido las validaciones de la Entidad AQUÍ!
    @NotBlank(message = "El nombre no puede estar vacío o ser solo espacios.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    private String nombre;

    @NotNull(message = "El precio no puede ser nulo.")
    @Min(value = 0, message = "El precio no puede ser negativo.")
    private double precio;

    // --- ¡CAMPO NUEVO! ---
    @NotNull(message = "El ID de la categoría no puede ser nulo.")
    private Long categoriaId;
}