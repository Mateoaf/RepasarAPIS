package com.example.refrescar_apis.models;

import lombok.Data;

@Data
public class Producto {
    private Long id;
    private String nombre;
    private double precio;

    public Producto(Long id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }
}
