package com.example.refrescar_apis.controllers;

import com.example.refrescar_apis.models.Producto;
import com.example.refrescar_apis.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
    @RequestMapping("api/productos")
    public class ProductoController {

        private final ProductoService productoService;
        public ProductoController(ProductoService productoService) {
            this.productoService = productoService;
        }

        @GetMapping
        public List<Producto> getAllProductos() {
            // "Jefe de sala": "Cocinero, dame el menú"
            return this.productoService.getAllProductos();
        }
        @GetMapping("/{id}")
        public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
            // "Jefe de sala": "Cocinero, busca la reserva 5"
            Optional<Producto> productoOpt = this.productoService.getProductoById(id);

            // Usamos ResponseEntity para devolver un "404 Not Found"
            // si el producto no existe. ¡Mucho más profesional!
            if (productoOpt.isPresent()) {
                return ResponseEntity.ok(productoOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        }

    @PostMapping
    public Producto createProducto(@Valid @RequestBody Producto nuevoProducto) {

        return this.productoService.createProducto(nuevoProducto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody Producto productoConDetalles) {

        Optional<Producto> productoActualizadoOpt =
                this.productoService.updateProducto(id, productoConDetalles);


        return productoActualizadoOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {

        boolean borrado = this.productoService.deleteProducto(id);

        if (borrado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
