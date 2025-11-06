package com.example.refrescar_apis.controllers;

import com.example.refrescar_apis.models.Producto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

    @RestController
    @RequestMapping("api/productos")
    public class ProductoController {
    private List<Producto> productos = new ArrayList<>(Arrays.asList(
            new Producto(1L, "Portátil", 1200.00),
            new Producto(2L, "Ratón", 55.00),
            new Producto(3L, "Teclado", 150.00)
    ));
    @GetMapping
    public List<Producto> getAllProductos() {
        return this.productos; // Spring lo convierte a JSON automáticamente
    }

    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable Long id) {
        return this.productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null); // Devuelve null si no lo encuentra
    }
    @PostMapping
        public Producto crearProducto(@RequestBody Producto nuevoProducto) {
        nuevoProducto.setId((long) (this.productos.size()+1));

        this.productos.add(nuevoProducto);
        return nuevoProducto;
    }

}
