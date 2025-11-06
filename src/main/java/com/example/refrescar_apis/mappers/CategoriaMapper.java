package com.example.refrescar_apis.mappers;

import com.example.refrescar_apis.dtos.CategoriaDTO;
import com.example.refrescar_apis.dtos.CategoriaRequestDTO;
import com.example.refrescar_apis.models.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring") // ¡Importante! Lo convierte en un Bean
public interface CategoriaMapper {

    // Sabe traducir en ambos sentidos

    CategoriaDTO entityToDto(Categoria categoria);

    Categoria dtoToEntity(CategoriaDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productos", ignore = true)
    Categoria requestDtoToEntity(CategoriaRequestDTO dto);
}