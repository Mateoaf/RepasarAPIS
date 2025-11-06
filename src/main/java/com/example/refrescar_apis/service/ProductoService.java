package com.example.refrescar_apis.service;

import com.example.refrescar_apis.models.Producto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private List<Producto> productos = new ArrayList<>(Arrays.asList(
            new Producto(1L, "Portátil", 1200.00),
            new Producto(2L, "Ratón", 55.00),
            new Producto(3L, "Teclado", 150.00)
    ));
    public List<Producto> getAllProductos() {
        return this.productos;
    }

    public Optional<Producto> getProductoById(Long id) {
        return this.productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }
    public Producto createProducto(Producto nuevoProducto) {
        // Asignamos un ID "falso"
        nuevoProducto.setId((long) (this.productos.size() + 1));
        this.productos.add(nuevoProducto);
        return nuevoProducto;
    }
}
