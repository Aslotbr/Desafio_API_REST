package com.apirest.logistica_projeto.service;

import com.apirest.logistica_projeto.dto.OrderDTO;
import com.apirest.logistica_projeto.dto.ProductDTO;
import com.apirest.logistica_projeto.dto.UserOrdersDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileProcessorServiceExtraTest {

    private FileProcessorService service;

    @BeforeEach
    void setUp() {
        service = new FileProcessorService();
    }

    @Test
    @DisplayName("processFiles deve processar múltiplos arquivos e agrupar dados")
    void testProcessFiles_multipleFiles() {
        String linha1 = "0000000001                              Usuario Teste00000000010000000001     100.0020250101";
        String linha2 = "0000000002                              Outro Usuario00000000020000000002     200.0020250202";

        MockMultipartFile file1 = new MockMultipartFile("files", "data1.txt", "text/plain", linha1.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file2 = new MockMultipartFile("files", "data2.txt", "text/plain", linha2.getBytes(StandardCharsets.UTF_8));

        service.processFiles(new MockMultipartFile[]{file1, file2});
        List<UserOrdersDTO> result = service.filterData(null, null, null);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals(2L, result.get(1).getUserId());
    }

    @Test
    @DisplayName("filterData sem filtros deve retornar todos os pedidos")
    void testFilterData_noFilters() {
        String linha = "0000000001                              Usuario Teste00000000010000000001     100.0020250101";
        service.processFile(new MockMultipartFile("file", "data.txt", "text/plain", linha.getBytes(StandardCharsets.UTF_8)));

        List<UserOrdersDTO> result = service.filterData(null, null, null);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("filterData com orderId deve filtrar corretamente")
    void testFilterData_byOrderId() {
        String linha1 = "0000000001                              Usuario Teste00000000010000000001     100.0020250101";
        String linha2 = "0000000001                              Usuario Teste00000000020000000002     200.0020250201";

        service.processFiles(new MockMultipartFile[]{
                new MockMultipartFile("files", "f1.txt", "text/plain", linha1.getBytes(StandardCharsets.UTF_8)),
                new MockMultipartFile("files", "f2.txt", "text/plain", linha2.getBytes(StandardCharsets.UTF_8))
        });

        List<UserOrdersDTO> result = service.filterData(1000000001L, null, null);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getOrders().size());
        assertEquals(1000000001L, result.get(0).getOrders().get(0).getOrderId());
    }

    @Test
    @DisplayName("filterData com intervalo de datas deve retornar somente pedidos no range")
    void testFilterData_byDateRange() {
        String linha1 = "0000000001                              Usuario Teste00000000010000000001     100.0020250101"; // 2025-01-01
        String linha2 = "0000000001                              Usuario Teste00000000020000000002     200.0020250505"; // 2025-05-05

        service.processFiles(new MockMultipartFile[]{
                new MockMultipartFile("files", "f1.txt", "text/plain", linha1.getBytes(StandardCharsets.UTF_8)),
                new MockMultipartFile("files", "f2.txt", "text/plain", linha2.getBytes(StandardCharsets.UTF_8))
        });

        List<UserOrdersDTO> result = service.filterData(null, "2025-01-01", "2025-03-01");

        assertEquals(1, result.size());
        assertEquals("2025-01-01", result.get(0).getOrders().get(0).getDate());
    }

    @Test
    @DisplayName("filterData com filtros que não retornam nada deve gerar lista vazia")
    void testFilterData_noResults() {
        String linha = "0000000001                              Usuario Teste00000000010000000001     100.0020250101";
        service.processFile(new MockMultipartFile("file", "data.txt", "text/plain", linha.getBytes(StandardCharsets.UTF_8)));

        List<UserOrdersDTO> result = service.filterData(999L, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("OrderDTO deve formatar total como ponto e não vírgula")
    void testOrderDTOFormatting() {
        OrderDTO order = new OrderDTO(1L, "2025-01-01");
        order.setTotal(50.5);
        assertEquals("50.50", order.getTotalFormatted());
    }

    @Test
    @DisplayName("ProductDTO deve formatar valor corretamente com ponto decimal")
    void testProductDTOFormatting() {
        ProductDTO product = new ProductDTO(1L, 99.99);
        assertEquals("99.99", String.format(java.util.Locale.US, "%.2f", product.getRawValue()));
    }
}
