package com.example.refrescar_apis.service;

import com.example.refrescar_apis.dtos.ProductoRequestDTO;
import com.example.refrescar_apis.dtos.ProductoResponseDTO;
import com.example.refrescar_apis.mappers.ProductoMapper;
import com.example.refrescar_apis.models.Categoria; // <-- Importar
import com.example.refrescar_apis.models.Producto;
import com.example.refrescar_apis.repositories.CategoriaRepository; // <-- Importar
import com.example.refrescar_apis.repositories.ProductoRepository;
import jakarta.persistence.EntityNotFoundException; // <-- Importar
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final CategoriaRepository categoriaRepository; // <-- 1. Inyectamos el nuevo repo

    // 2. Modificamos el constructor
    public ProductoService(ProductoRepository productoRepository,
                           ProductoMapper productoMapper,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
        this.categoriaRepository = categoriaRepository;
    }

    // --- (getAllProductos y getProductoById) ---
    // ¡Estos siguen funcionando igual! MapStruct (con CategoriaMapper)
    // se encarga de anidar el CategoriaDTO dentro del ProductoResponseDTO
    // automáticamente. ¡No hay que tocarlos!

    public List<ProductoResponseDTO> getAllProductos() {
        return this.productoRepository.findAll()
                .stream()
                .map(productoMapper::entityToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<ProductoResponseDTO> getProductoById(Long id) {
        return this.productoRepository.findById(id)
                .map(productoMapper::entityToResponseDto);
    }

    // --- ¡AQUÍ ESTÁ EL CAMBIO IMPORTANTE! ---

    public ProductoResponseDTO createProducto(ProductoRequestDTO requestDTO) {

        // 1. Buscamos la Categoría
        Long categoriaId = requestDTO.getCategoriaId();
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + categoriaId));

        // 2. Traducimos el DTO a Entidad (aún sin categoría)
        //    (Recuerda: el mapper está configurado para ignorar la categoría)
        Producto productoEntity = productoMapper.requestDtoToEntity(requestDTO);

        // 3. "Enganchamos" la categoría que sí encontramos
        productoEntity.setCategoria(categoria);

        // 4. Guardamos la Entidad completa
        Producto productoGuardado = this.productoRepository.save(productoEntity);

        // 5. Traducimos a DTO de respuesta y devolvemos
        return productoMapper.entityToResponseDto(productoGuardado);
    }

    public Optional<ProductoResponseDTO> updateProducto(Long id, ProductoRequestDTO requestDTO) {

        // 1. Buscamos el Producto a actualizar
        Optional<Producto> productoExistenteOpt = this.productoRepository.findById(id);
        if (productoExistenteOpt.isEmpty()) {
            return Optional.empty(); // No existe el producto
        }

        // 2. Buscamos la NUEVA Categoría
        Long categoriaId = requestDTO.getCategoriaId();
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada con id: " + categoriaId));

        // 3. Actualizamos la entidad existente
        Producto productoExistente = productoExistenteOpt.get();
        productoExistente.setNombre(requestDTO.getNombre());
        productoExistente.setPrecio(requestDTO.getPrecio());
        productoExistente.setCategoria(categoria); // Actualizamos la categoría

        // 4. Guardamos
        Producto productoActualizado = this.productoRepository.save(productoExistente);

        // 5. Traducimos y devolvemos
        return Optional.of(productoMapper.entityToResponseDto(productoActualizado));
    }

    // (El método deleteProducto no cambia)
    public boolean deleteProducto(Long id) {
        if (this.productoRepository.existsById(id)) {
            this.productoRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}