package com.apirest.logistica_projeto.controller;

import com.apirest.logistica_projeto.dto.UserOrdersDTO;
import com.apirest.logistica_projeto.service.FileProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/arquivo")
public class FileUploadController {

    @Autowired
    private FileProcessorService fileProcessorService;

    // POST: Faz upload de múltiplos arquivos .txt
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            fileProcessorService.processFiles(files);
            return ResponseEntity.ok(Map.of("message", "Arquivos processados com sucesso!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao processar arquivos: " + e.getMessage()));
        }
    }

    // GET: Retorna dados processados com filtros opcionais
    @GetMapping(value = "/dados", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserOrdersDTO>> getDados(
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

        List<UserOrdersDTO> dados = fileProcessorService.filterData(orderId, startDate, endDate);
        return ResponseEntity.ok(dados);
    }
}
