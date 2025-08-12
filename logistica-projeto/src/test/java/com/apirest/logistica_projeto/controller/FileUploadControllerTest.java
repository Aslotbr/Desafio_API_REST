package com.apirest.logistica_projeto.controller;

import com.apirest.logistica_projeto.service.FileProcessorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileProcessorService fileProcessorService;

    @Test
    @DisplayName("Deve aceitar upload de arquivo e retornar 200")
    void uploadFileSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files", // igual ao @RequestParam("files")
                "data.txt",
                "text/plain",
                "conteudo".getBytes()
        );

        Mockito.doNothing().when(fileProcessorService)
                .processFiles(Mockito.any(MultipartFile[].class));

        mockMvc.perform(multipart("/arquivo/upload").file(file))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 400 se nenhum arquivo for enviado")
    void uploadFileNoFile() throws Exception {
        // Para evitar 404, precisamos que o controller trate essa situação
        mockMvc.perform(multipart("/arquivo/upload"))
                .andExpect(status().isBadRequest());
    }
}
