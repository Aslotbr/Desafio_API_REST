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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FileProcessorService {
private final List<UserOrdersDTO> allData = new ArrayList<>();
public void processFiles(MultipartFile[] files) throws Exception {
    allData.clear(); // limpa dados anteriores

    for (MultipartFile file : files) {
        System.out.println("📄 Processando arquivo: " + file.getOriginalFilename());
        processSingleFile(file);
    }
}

private void processSingleFile(MultipartFile file) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
    String line;
    Map<Long, UserOrdersDTO> userMap = new HashMap<>();

    Pattern pattern = Pattern.compile("^(\\d{10})\\s+(.*?)\\s+(\\d{10})(\\d{10})(\\s*[\\d\\.]+)(\\d{8})$");

    while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.length() < 78) {
            System.err.println("⚠️ Ignorada (linha muito curta): " + line);
            continue;
        }

        try {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                Long userId = Long.parseLong(matcher.group(1).trim());
                String userName = matcher.group(2).trim();
                Long orderId = Long.parseLong(matcher.group(3).trim());
                Long productId = Long.parseLong(matcher.group(4).trim());
                Double value = Double.parseDouble(matcher.group(5).trim());
                String date = matcher.group(6).trim();

                UserOrdersDTO user = userMap.computeIfAbsent(userId, id -> new UserOrdersDTO(userId, userName));

                OrderDTO order = user.getOrders().stream()
                        .filter(o -> o.getOrderId().equals(orderId))
                        .findFirst()
                        .orElseGet(() -> {
                            OrderDTO newOrder = new OrderDTO(orderId, date, 0.0);
                            user.getOrders().add(newOrder);
                            return newOrder;
                        });

                order.getProducts().add(new ProductDTO(productId, value));
                order.setTotal(order.getTotal() + value);
            } else {
                System.err.println("❌ Ignorada (regex não casou): " + line);
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao processar linha: [" + line + "]");
            e.printStackTrace();
        }
    }

    allData.addAll(userMap.values());
}

public List<UserOrdersDTO> filterData(Long orderId, String startDate, String endDate) {
    if (allData.isEmpty()) return Collections.emptyList();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date parsedStart = null;
    Date parsedEnd = null;

    try {
        if (startDate != null) parsedStart = sdf.parse(startDate);
        if (endDate != null) parsedEnd = sdf.parse(endDate);
    } catch (Exception e) {
        System.err.println("⚠️ Datas inválidas no filtro: " + e.getMessage());
        return Collections.emptyList();
    }

    final Date finalStart = parsedStart;
    final Date finalEnd = parsedEnd;

    return allData.stream()
            .map(user -> {
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
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
}
}