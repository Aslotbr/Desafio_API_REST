package com.apirest.logistica_projeto.service;

import com.apirest.logistica_projeto.dto.OrderDTO;
import com.apirest.logistica_projeto.dto.ProductDTO;
import com.apirest.logistica_projeto.dto.UserOrdersDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileProcessorServiceTest {

    private FileProcessorService service;

    @BeforeEach
    void setUp() {
        service = new FileProcessorService();
    }

    @Test
    void testProcessFile_singleLine_shouldReturnValidUserOrder() {
        // Arrange — linha extraída do seu arquivo data_1.txt
        String linha = "0000000070                              Palmer Prosacco00000007530000000003     1836.7420210308";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "data_1.txt",
                "text/plain",
                linha.getBytes(StandardCharsets.UTF_8)
        );

        // Act
        List<UserOrdersDTO> result = service.processFile(file);

        // Assert
        assertEquals(1, result.size());
        UserOrdersDTO user = result.get(0);
        assertEquals(70L, user.getUserId());
        assertEquals("Palmer Prosacco", user.getName());

        List<OrderDTO> orders = user.getOrders();
        assertEquals(1, orders.size());

        OrderDTO order = orders.get(0);
        assertEquals(753L, order.getOrderId());
        assertEquals("2021-03-08", order.getDate());
        assertEquals(1, order.getProducts().size());

        ProductDTO product = order.getProducts().get(0);
        assertEquals(3L, product.getProductId());
        assertEquals(1836.74, product.getRawValue(), 0.001);
    }
}
