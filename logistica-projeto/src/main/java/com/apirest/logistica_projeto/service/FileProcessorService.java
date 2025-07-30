package com.apirest.logistica_projeto.service;

import com.apirest.logistica_projeto.dto.OrderDTO;
import com.apirest.logistica_projeto.dto.ProductDTO;
import com.apirest.logistica_projeto.dto.UserOrdersDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileProcessorService {
    private final List<UserOrdersDTO> allData = new ArrayList<>();

    // POST: Processa arquivo .txt enviado
    public void processFile(MultipartFile file) throws Exception {
        allData.clear();

        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
        String line;
        Map<Long, UserOrdersDTO> userMap = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            if (line.length() < 98) continue; // comprimento mínimo necessário

            try {
                Long userId = Long.parseLong(line.substring(0, 10).trim());
                String userName = line.substring(34, 55).trim(); // ajustar se necessário
                Long orderId = Long.parseLong(line.substring(55, 65).trim());
                Long productId = Long.parseLong(line.substring(65, 75).trim());
                Double value = Double.parseDouble(line.substring(75, 90).trim());
                String date = line.substring(90).trim(); // yyyyMMdd

                UserOrdersDTO user = userMap.computeIfAbsent(userId, id -> new UserOrdersDTO(userId, userName));

                Optional<OrderDTO> existingOrder = user.getOrders().stream()
                        .filter(o -> o.getOrderId().equals(orderId))
                        .findFirst();

                OrderDTO order;
                if (existingOrder.isPresent()) {
                    order = existingOrder.get();
                } else {
                    order = new OrderDTO(orderId, date, 0.0);
                    user.getOrders().add(order);
                }

                order.getProducts().add(new ProductDTO(productId, value));
                order.setTotal(order.getTotal() + value);

            } catch (Exception e) {
                // Ignora linhas mal formatadas
                System.err.println("Erro ao processar linha: " + line);
            }
        }

        allData.addAll(userMap.values());
    }

    // GET sem filtro: retorna todos os dados processados
    public List<UserOrdersDTO> getAllData() {
        return allData;
    }

    // GET com filtro: retorna dados filtrados
    public List<UserOrdersDTO> filterData(Long orderId, String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // importante manter esse formato para o parse
        Date parsedStart = null;
        Date parsedEnd = null;

        try {
            if (startDate != null) parsedStart = sdf.parse(startDate);
            if (endDate != null) parsedEnd = sdf.parse(endDate);
        } catch (Exception e) {
            return Collections.emptyList(); // Falha na conversão de data
        }

        final Date finalStart = parsedStart;
        final Date finalEnd = parsedEnd;

        return allData.stream().map(user -> {
            List<OrderDTO> filteredOrders = user.getOrders().stream()
                    .filter(order -> {
                        boolean matchOrderId = (orderId == null || order.getOrderId().equals(orderId));
                        boolean matchDate = true;
                        try {
                            Date orderDate = sdf.parse(order.getDate());
                            if (finalStart != null && orderDate.before(finalStart)) matchDate = false;
                            if (finalEnd != null && orderDate.after(finalEnd)) matchDate = false;
                        } catch (Exception ignored) {}
                        return matchOrderId && matchDate;
                    })
                    .collect(Collectors.toList());

            if (filteredOrders.isEmpty()) return null;

            UserOrdersDTO filteredUser = new UserOrdersDTO(user.getUserId(), user.getName());
            filteredUser.setOrders(filteredOrders);
            return filteredUser;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
