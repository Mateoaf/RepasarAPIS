package com.example.refrescar_apis.controllers;

import com.example.refrescar_apis.dtos.ProductoRequestDTO;
import com.example.refrescar_apis.models.Categoria; // <-- Importar
import com.example.refrescar_apis.models.Producto;
import com.example.refrescar_apis.repositories.CategoriaRepository; // <-- Importar
import com.example.refrescar_apis.repositories.ProductoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "jwt.secret=una-clave-secreta-falsa-para-tests-no-importa"
})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository; // <-- 1. Inyectamos el nuevo repo

    @Autowired
    private ObjectMapper objectMapper;

    // Guardaremos la categoría de prueba para usarla en varios tests
    private Categoria categoriaDePrueba;

    @BeforeEach
    void setUp() {
        // 2. Limpiamos las tablas en el orden correcto
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();

        // 3. Creamos una categoría base para la mayoría de tests
        categoriaDePrueba = Categoria.builder().nombre("Periféricos").build();
        categoriaRepository.save(categoriaDePrueba); // Ahora tiene un ID
    }

    // --- (Test de GET actualizado) ---
    @Test
    void testGetAllProductos_publico_deberiaDevolverOk() throws Exception {
        // ARRANGE: Creamos un producto y lo ligamos a la categoría
        productoRepository.save(Producto.builder()
                .nombre("Test")
                .precio(10)
                .categoria(categoriaDePrueba) // <-- ¡Ligado!
                .build());

        mockMvc.perform( get("/api/productos") )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("$.length()").value(1) )
                .andExpect( jsonPath("$[0].nombre").value("Test") )
                // 4. ¡Comprobamos la categoría anidada!
                .andExpect( jsonPath("$[0].categoria.nombre").value("Periféricos") );
    }

    // Método helper para crear un DTO válido que apunte a nuestra categoría
    private ProductoRequestDTO crearDtoValido() {
        ProductoRequestDTO dto = new ProductoRequestDTO();
        dto.setNombre("Producto DTO Valido");
        dto.setPrecio(100.0);
        dto.setCategoriaId(categoriaDePrueba.getId()); // <-- ¡Usa el ID real!
        return dto;
    }

    @Test
    void testCreateProducto_sinLogin_deberiaDevolverForbidden() throws Exception {
        // ARRANGE
        ProductoRequestDTO dtoValido = crearDtoValido();
        String jsonDto = objectMapper.writeValueAsString(dtoValido);

        // ACT & ASSERT
        mockMvc.perform( post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)
                        .with(anonymous()) )
                .andExpect( status().isForbidden() ); // 403
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreateProducto_conLoginUser_deberiaDevolverForbidden() throws Exception {
        // ARRANGE
        ProductoRequestDTO dtoValido = crearDtoValido();
        String jsonDto = objectMapper.writeValueAsString(dtoValido);

        // ACT & ASSERT
        mockMvc.perform( post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto) )
                .andExpect( status().isForbidden() ); // 403
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateProducto_conLoginAdmin_deberiaCrearProducto() throws Exception {
        // ARRANGE
        ProductoRequestDTO dtoValido = crearDtoValido();
        String jsonDto = objectMapper.writeValueAsString(dtoValido);

        // ACT & ASSERT
        mockMvc.perform( post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("$.id").exists() )
                .andExpect( jsonPath("$.nombre").value("Producto DTO Valido") )
                // 5. ¡Comprobamos la respuesta anidada!
                .andExpect( jsonPath("$.categoria.id").value(categoriaDePrueba.getId()) )
                .andExpect( jsonPath("$.categoria.nombre").value("Periféricos") );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateProducto_conDtoInvalido_deberiaDevolverBadRequest() throws Exception {
        // ARRANGE
        ProductoRequestDTO dtoInvalido = new ProductoRequestDTO();
        dtoInvalido.setNombre("a"); // Falla @Size
        dtoInvalido.setPrecio(-50); // Falla @Min
        // ¡Falla @NotNull! (no le ponemos categoriaId)

        String jsonDto = objectMapper.writeValueAsString(dtoInvalido);

        // ACT & ASSERT
        mockMvc.perform( post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("$.nombre").value("El nombre debe tener entre 3 y 100 caracteres.") )
                .andExpect( jsonPath("$.precio").value("El precio no puede ser negativo.") )
                // 6. ¡Comprobamos la nueva regla de validación!
                .andExpect( jsonPath("$.categoriaId").value("El ID de la categoría no puede ser nulo.") );
    }
}