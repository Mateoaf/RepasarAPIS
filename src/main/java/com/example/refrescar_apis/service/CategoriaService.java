package com.example.refrescar_apis.services;

import com.example.refrescar_apis.dtos.CategoriaDTO;
import com.example.refrescar_apis.dtos.CategoriaRequestDTO;
import com.example.refrescar_apis.mappers.CategoriaMapper;
import com.example.refrescar_apis.models.Categoria;
import com.example.refrescar_apis.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    /**
     * Devuelve todas las categorías.
     */
    public List<CategoriaDTO> getAllCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::entityToDto)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva categoría.
     */
    public CategoriaDTO createCategoria(CategoriaRequestDTO requestDTO) {
        // (En un caso real, aquí comprobaríamos si ya existe una con ese nombre)

        // 1. Traduce DTO -> Entidad (sin ID)
        Categoria nuevaCategoria = categoriaMapper.requestDtoToEntity(requestDTO);

        // 2. Guarda la entidad
        Categoria categoriaGuardada = categoriaRepository.save(nuevaCategoria);

        // 3. Traduce Entidad -> DTO (con ID) y devuelve
        return categoriaMapper.entityToDto(categoriaGuardada);
    }
}