package com.example.refrescar_apis.service;

import com.example.refrescar_apis.models.Producto;
import com.example.refrescar_apis.repositories.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> getAllProductos() {
        return this.productoRepository.findAll();
    }

    public Optional<Producto> getProductoById(Long id) {
        return this.productoRepository.findById(id);
    }

    public Producto createProducto(Producto nuevoProducto) {
        return this.productoRepository.save(nuevoProducto);
    }
    // ... dentro de la clase ProductoService ...

    /**
     * Actualiza un producto existente en la BBDD.
     */
    public Optional<Producto> updateProducto(Long id, Producto productoConDetalles) {

        // 1. Buscar el producto por ID
        Optional<Producto> productoExistenteOpt = this.productoRepository.findById(id);

        // 2. Comprobar si existe
        if (productoExistenteOpt.isEmpty()) {
            return Optional.empty(); // No existe, devolvemos Optional vacío
        }

        // 3. Si existe, lo sacamos del Optional
        Producto productoExistente = productoExistenteOpt.get();

        // 4. Actualizamos los campos
        productoExistente.setNombre(productoConDetalles.getNombre());
        productoExistente.setPrecio(productoConDetalles.getPrecio());
        // (El ID no se toca, sigue siendo el mismo)

        // 5. Guardamos el producto actualizado.
        Producto productoActualizado = this.productoRepository.save(productoExistente);
        return Optional.of(productoActualizado);
    }


    public boolean deleteProducto(Long id) {

        // 1. Comprobamos si existe antes de borrar
        if (this.productoRepository.existsById(id)) {
            // 2. Si existe, lo borramos
            this.productoRepository.deleteById(id);
            return true;
        } else {
            // 3. Si no existe, no podemos borrarlo
            return false;
        }
    }
}
