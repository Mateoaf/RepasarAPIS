package com.example.refrescar_apis.controllers;

import com.example.refrescar_apis.models.Producto;
import com.example.refrescar_apis.service.ProductoService;
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
    public Producto createProducto(@RequestBody Producto nuevoProducto) {
        // "Jefe de sala": "Cocinero, prepara este nuevo plato"
        return this.productoService.createProducto(nuevoProducto);
    }
}
