package com.example.refrescar_apis.controllers;

// 1. ¡Cambiamos los imports!
import com.example.refrescar_apis.dtos.ProductoRequestDTO;
import com.example.refrescar_apis.dtos.ProductoResponseDTO;
import com.example.refrescar_apis.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // 2. Cambia la firma: devuelve DTOs
    @GetMapping
    public List<ProductoResponseDTO> getAllProductos() {
        return this.productoService.getAllProductos();
    }

    // 3. Cambia la firma: devuelve DTOs
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> getProductoById(@PathVariable Long id) {

        Optional<ProductoResponseDTO> productoOpt = this.productoService.getProductoById(id);

        // (Esta lógica de respuesta 404 sigue funcionando igual)
        return productoOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Cambia la firma: Acepta y devuelve DTOs
    //    ¡@Valid ahora valida el DTO de entrada!
    @PostMapping
    public ProductoResponseDTO createProducto(@Valid @RequestBody ProductoRequestDTO nuevoProductoDto) {
        return this.productoService.createProducto(nuevoProductoDto);
    }

    // 5. Cambia la firma: Acepta y devuelve DTOs
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO requestDTO) { // <-- Acepta DTO

        Optional<ProductoResponseDTO> productoActualizadoOpt =
                this.productoService.updateProducto(id, requestDTO);

        return productoActualizadoOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // (El método deleteProducto no cambia)
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