package com.apirest.logistica_projeto.dto;

import java.util.ArrayList;
import java.util.List;

public class UserOrdersDTO {
    private Long userId;
    private String name;
    private List<OrderDTO> orders = new ArrayList<>();

    public UserOrdersDTO() {}

    public UserOrdersDTO(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }
}
