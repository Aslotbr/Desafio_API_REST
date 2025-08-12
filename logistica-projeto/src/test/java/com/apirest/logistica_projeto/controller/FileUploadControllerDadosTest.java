package com.apirest.logistica_projeto.controller;

import com.apirest.logistica_projeto.dto.OrderDTO;
import com.apirest.logistica_projeto.dto.ProductDTO;
import com.apirest.logistica_projeto.dto.UserOrdersDTO;
import com.apirest.logistica_projeto.service.FileProcessorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerDadosTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileProcessorService fileProcessorService;

    @Test
    @DisplayName("Deve retornar lista de pedidos sem filtros")
    void getDadosSemFiltros() throws Exception {
        // Monta dados fictícios compatíveis com os DTOs
        ProductDTO produto = new ProductDTO(1001L, 50.0);
        OrderDTO pedido = new OrderDTO();
        pedido.setOrderId(2001L);
        pedido.setDate("2025-08-10");
        pedido.setProducts(List.of(produto));
        pedido.setTotal(50.0);

        UserOrdersDTO usuario = new UserOrdersDTO(1L, "Felipe");
        usuario.getOrders().add(pedido);

        Mockito.when(fileProcessorService.filterData(null, null, null))
                .thenReturn(List.of(usuario));

        mockMvc.perform(get("/arquivo/dados")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user_id", is(1))) // campo mudou
                .andExpect(jsonPath("$[0].name", is("Felipe")))
                .andExpect(jsonPath("$[0].orders[0].order_id", is(2001))) // campo mudou
                .andExpect(jsonPath("$[0].orders[0].products[0].product_id", is(1001))) // campo mudou
                .andExpect(jsonPath("$[0].orders[0].total", is("50.00"))); // agora string
    }

    @Test
    @DisplayName("Deve aplicar filtros de orderId e datas")
    void getDadosComFiltros() throws Exception {
        Mockito.when(fileProcessorService.filterData(2001L, "2025-08-01", "2025-08-31"))
                .thenReturn(List.of()); // simulando nenhum resultado

        mockMvc.perform(get("/arquivo/dados")
                        .param("orderId", "2001")
                        .param("startDate", "2025-08-01")
                        .param("endDate", "2025-08-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

