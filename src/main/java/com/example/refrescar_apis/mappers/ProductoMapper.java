package com.example.refrescar_apis.mappers;

import com.example.refrescar_apis.dtos.ProductoRequestDTO;
import com.example.refrescar_apis.dtos.ProductoResponseDTO;
import com.example.refrescar_apis.models.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping; // ¡Importa la anotación Mapping!

// 1. Le decimos a Spring que este es un Bean
// 2. Le decimos que "use" el otro mapper cuando lo necesite
@Mapper(componentModel = "spring", uses = CategoriaMapper.class)
public interface ProductoMapper {


    ProductoResponseDTO entityToResponseDto(Producto entity);

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "id", ignore = true)
    Producto requestDtoToEntity(ProductoRequestDTO dto);

}