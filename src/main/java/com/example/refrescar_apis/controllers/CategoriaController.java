package com.example.refrescar_apis.controllers;

import com.example.refrescar_apis.dtos.CategoriaDTO;
import com.example.refrescar_apis.dtos.CategoriaRequestDTO;
import com.example.refrescar_apis.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaDTO> getAllCategorias() {
        return categoriaService.getAllCategorias();
    }

    @PostMapping
    public CategoriaDTO createCategoria(@Valid @RequestBody CategoriaRequestDTO requestDTO) {
        return categoriaService.createCategoria(requestDTO);
    }
}