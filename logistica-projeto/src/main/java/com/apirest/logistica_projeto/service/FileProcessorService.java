package com.apirest.logistica_projeto.service;

import com.apirest.logistica_projeto.dto.OrderDTO;
import com.apirest.logistica_projeto.dto.ProductDTO;
import com.apirest.logistica_projeto.dto.UserOrdersDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileProcessorService {
    private final Map<Long, UserOrdersDTO> dadosProcessados = new HashMap<>();

// Regex aceita ponto ou vírgula como separador decimal
private static final Pattern pattern = Pattern.compile(
    "(\\d{10})\\s*(.{1,45}?)(\\d{10})(\\d{10})\\s+(\\d+[\\.,]\\d{2})(\\d{8})"
);

private static final DateTimeFormatter inputDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
private static final DateTimeFormatter outputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

public void processFiles(MultipartFile[] files) {
    for (MultipartFile file : files) {
        List<UserOrdersDTO> parsed = processFile(file);
        mergeData(parsed);
    }
}

public List<UserOrdersDTO> processFile(MultipartFile file) {
    Map<Long, UserOrdersDTO> tempMap = new HashMap<>();

    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) continue;

            Long userId = Long.parseLong(matcher.group(1));
            String userName = matcher.group(2).trim();
            Long orderId = Long.parseLong(matcher.group(3));
            Long productId = Long.parseLong(matcher.group(4));

            // Trata vírgula como ponto para conversão segura
            String valorBruto = matcher.group(5).replace(",", ".");
            Double value = Double.parseDouble(valorBruto);

            String dateStr = LocalDate.parse(matcher.group(6), inputDateFormat)
                                       .format(outputDateFormat);

            UserOrdersDTO user = tempMap.computeIfAbsent(userId, id -> {
                UserOrdersDTO u = new UserOrdersDTO();
                u.setUserId(id);
                u.setName(userName);
                return u;
            });

            OrderDTO order = user.getOrders().stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElseGet(() -> {
                    OrderDTO o = new OrderDTO();
                    o.setOrderId(orderId);
                    o.setDate(dateStr);
                    user.getOrders().add(o);
                    return o;
                });

            ProductDTO product = new ProductDTO(productId, value);
            order.getProducts().add(product);

            // Total calculado com precisão
            double novoTotal = order.getProducts().stream()
                .mapToDouble(ProductDTO::getRawValue)
                .sum();
            order.setTotal(novoTotal);
        }

    } catch (Exception e) {
        throw new RuntimeException("Erro ao processar arquivo: " + e.getMessage(), e);
    }

    return new ArrayList<>(tempMap.values());
}

private void mergeData(List<UserOrdersDTO> novos) {
    for (UserOrdersDTO novo : novos) {
        dadosProcessados.merge(novo.getUserId(), novo, (existente, recebido) -> {
            existente.getOrders().addAll(recebido.getOrders());
            return existente;
        });
    }
}

public List<UserOrdersDTO> filterData(Long orderId, String startDate, String endDate) {
    List<UserOrdersDTO> filtrados = new ArrayList<>();
    LocalDate start = parseDate(startDate);
    LocalDate end = parseDate(endDate);

    for (UserOrdersDTO user : dadosProcessados.values()) {
        UserOrdersDTO clone = new UserOrdersDTO(user.getUserId(), user.getName());

        for (OrderDTO order : user.getOrders()) {
            boolean matchOrder = (orderId == null || order.getOrderId().equals(orderId));
            LocalDate orderDate = LocalDate.parse(order.getDate(), outputDateFormat);
            boolean matchDate = (start == null || !orderDate.isBefore(start)) &&
                                (end == null || !orderDate.isAfter(end));

            if (matchOrder && matchDate) {
                clone.getOrders().add(order);
            }
        }

        if (!clone.getOrders().isEmpty()) {
            filtrados.add(clone);
        }
    }

    return filtrados;
}

private LocalDate parseDate(String date) {
    if (date == null || date.isBlank()) return null;
    return LocalDate.parse(date, outputDateFormat);
}
}