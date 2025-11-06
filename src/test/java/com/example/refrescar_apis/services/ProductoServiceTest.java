package com.example.refrescar_apis.services;

import com.example.refrescar_apis.dtos.ProductoRequestDTO;
import com.example.refrescar_apis.dtos.ProductoResponseDTO;
import com.example.refrescar_apis.mappers.ProductoMapper;
import com.example.refrescar_apis.models.Producto;
import com.example.refrescar_apis.repositories.ProductoRepository;

import com.example.refrescar_apis.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*; // Usamos las aserciones de JUnit 5
import static org.mockito.ArgumentMatchers.any; // Import estático para 'any'
import static org.mockito.Mockito.*; // Import estático para 'when', 'verify', 'never'


@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // 1. Creamos un "actor de doblaje" (FALSO) para el Repositorio
    @Mock
    private ProductoRepository productoRepository;

    // 2. ¡NUEVO! Creamos un "actor de doblaje" (FALSO) para el Mapper
    @Mock
    private ProductoMapper productoMapper;

    // 3. Creamos una instancia REAL de ProductoService
    // e "inyéctale" todos los @Mock (el falso repo Y el falso mapper)
    @InjectMocks
    private ProductoService productoService;

    // --- Datos de prueba reutilizables ---
    private Producto productoEntidad;
    private ProductoRequestDTO requestDTO;
    private ProductoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Datos que usaremos en varios tests

        // El DTO de entrada que simula enviar el usuario
        requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Test DTO");
        requestDTO.setPrecio(100.0);

        // La entidad que "simulamos" que existe en la BBDD
        productoEntidad = Producto.builder()
                .id(1L)
                .nombre("Test DTO")
                .precio(100.0)
                .build();

        // El DTO de respuesta que "simulamos" que el mapper crea
        responseDTO = new ProductoResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNombre("Test DTO");
        responseDTO.setPrecio(100.0);
    }


    @Test
    void testGetAllProductos_deberiaDevolverListaDeDTOs() {
        // --- 1. ARRANGE (Organizar) ---
        // Le decimos al FALSO repo qué devolver
        when(productoRepository.findAll()).thenReturn(List.of(productoEntidad));
        // Le decimos al FALSO mapper qué devolver
        when(productoMapper.entityToResponseDto(productoEntidad)).thenReturn(responseDTO);

        // --- 2. ACT (Actuar) ---
        List<ProductoResponseDTO> resultado = productoService.getAllProductos();

        // --- 3. ASSERT (Afirmar) ---
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Test DTO", resultado.get(0).getNombre());

        // Verificamos que los mocks fueron llamados
        verify(productoRepository).findAll();
        verify(productoMapper).entityToResponseDto(productoEntidad);
    }

    @Test
    void testGetProductoById_cuandoExiste() {
        // --- 1. ARRANGE ---
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoEntidad));
        when(productoMapper.entityToResponseDto(productoEntidad)).thenReturn(responseDTO);

        // --- 2. ACT ---
        Optional<ProductoResponseDTO> resultado = productoService.getProductoById(1L);

        // --- 3. ASSERT ---
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        verify(productoRepository).findById(1L);
        verify(productoMapper).entityToResponseDto(productoEntidad);
    }

    @Test
    void testGetProductoById_cuandoNoExiste() {
        // --- 1. ARRANGE ---
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // --- 2. ACT ---
        Optional<ProductoResponseDTO> resultado = productoService.getProductoById(99L);

        // --- 3. ASSERT ---
        assertTrue(resultado.isEmpty());
        // ¡Verificamos que el mapper NUNCA fue llamado!
        verify(productoMapper, never()).entityToResponseDto(any(Producto.class));
    }

    @Test
    void testCreateProducto_deberiaGuardarYDevolverDTO() {
        // --- 1. ARRANGE ---
        // 1. Simulamos la traducción DTO -> Entidad (sin ID)
        Producto entidadSinId = Producto.builder().nombre("Test DTO").precio(100.0).build();
        when(productoMapper.requestDtoToEntity(requestDTO)).thenReturn(entidadSinId);

        // 2. Simulamos el guardado en BBDD (que devuelve la entidad CON ID)
        when(productoRepository.save(entidadSinId)).thenReturn(productoEntidad); // productoEntidad SÍ tiene ID 1L

        // 3. Simulamos la traducción Entidad -> DTO (con ID)
        when(productoMapper.entityToResponseDto(productoEntidad)).thenReturn(responseDTO);

        // --- 2. ACT ---
        ProductoResponseDTO resultado = productoService.createProducto(requestDTO);

        // --- 3. ASSERT ---
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Test DTO", resultado.getNombre());

        // Verificamos que todo el flujo de llamadas ocurrió
        verify(productoMapper).requestDtoToEntity(requestDTO);
        verify(productoRepository).save(entidadSinId);
        verify(productoMapper).entityToResponseDto(productoEntidad);
    }
}