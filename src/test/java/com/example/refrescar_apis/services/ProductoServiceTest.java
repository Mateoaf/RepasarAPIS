package com.example.refrescar_apis.services;

import com.example.refrescar_apis.models.Producto;
import com.example.refrescar_apis.repositories.ProductoRepository;
import com.example.refrescar_apis.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

// 1. Le dice a JUnit que use la extensión de Mockito
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // 2. Crea un "actor de doblaje" (un FALSO ProductoRepository)
    @Mock
    private ProductoRepository productoRepository;

    // 3. Crea una instancia REAL de ProductoService
    // e "inyéctale" todos los @Mock (el falso repo)
    @InjectMocks
    private ProductoService productoService;

    // ¡Empecemos a probar!

    @Test
    void testGetAllProductos_deberiaDevolverLista() {
        // --- 1. ARRANGE (Organizar) ---
        // Preparamos nuestros datos falsos
        Producto p1 = Producto.builder()
                .id(1L)
                .nombre("Test 1")
                .precio(100.0)
                .build();

        List<Producto> listaFalsa = List.of(p1);

        // Le decimos a nuestro "actor" (el repo) qué debe hacer:
        // "CUANDO alguien llame a tu método findAll(),
        // ENTONCES devuelve la listaFalsa que acabo de crear."
        org.mockito.Mockito.when(productoRepository.findAll())
                .thenReturn(listaFalsa);

        // --- 2. ACT (Actuar) ---
        // Llamamos al método REAL que queremos probar
        List<Producto> resultado = productoService.getAllProductos();

        // --- 3. ASSERT (Afirmar) ---
        // Comprobamos que el resultado es el que esperábamos

        assert(resultado.size() == 1);
        assert(resultado.get(0).getNombre().equals("Test 1"));
    }

    @Test
    void testGetProductoById_cuandoExiste() {
        // --- 1. ARRANGE ---
        Long idBuscado = 1L;
        Producto p1 = Producto.builder()
                .id(idBuscado)
                .nombre("Test 1")
                .precio(100.0)
                .build();

        // "CUANDO alguien llame a findById(1L),
        // ENTONCES devuelve un Optional que contiene p1"
        org.mockito.Mockito.when(productoRepository.findById(idBuscado))
                .thenReturn(Optional.of(p1));

        // --- 2. ACT ---
        Optional<Producto> resultado = productoService.getProductoById(idBuscado);

        // --- 3. ASSERT ---
        assert(resultado.isPresent());
        assert(resultado.get().getId().equals(idBuscado));
    }

    @Test
    void testGetProductoById_cuandoNoExiste() {
        // --- 1. ARRANGE ---
        Long idBuscado = 99L;

        // "CUANDO alguien llame a findById(99L),
        // ENTONCES devuelve un Optional vacío"
        org.mockito.Mockito.when(productoRepository.findById(idBuscado))
                .thenReturn(Optional.empty());

        // --- 2. ACT ---
        Optional<Producto> resultado = productoService.getProductoById(idBuscado);

        // --- 3. ASSERT ---
        assert(resultado.isEmpty());
    }

    @Test
    void testCreateProducto_deberiaGuardarCorrectamente() {
        // --- 1. ARRANGE ---
        Producto productoSinId = Producto.builder()
                .nombre("Nuevo")
                .precio(200.0)
                .build(); // Fíjate qué limpio, no le ponemos ID

        Producto productoConId = Producto.builder()
                .id(1L)
                .nombre("Nuevo")
                .precio(200.0)
                .build();

        // "CUANDO alguien llame a save(CUALQUIER objeto Producto),
        // ENTONCES devuelve el productoConId"
        org.mockito.Mockito.when(productoRepository.save(org.mockito.ArgumentMatchers.any(Producto.class)))
                .thenReturn(productoConId);

        // --- 2. ACT ---
        Producto resultado = productoService.createProducto(productoSinId);

        // --- 3. ASSERT ---
        assert(resultado.getId() != null);
        assert(resultado.getId().equals(1L));
        assert(resultado.getNombre().equals("Nuevo"));
    }
}