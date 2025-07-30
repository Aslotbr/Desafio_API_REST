package com.apirest.logistica_projeto.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private Long orderId;
    private String date;
    private Double total;
    private List<ProductDTO> products = new ArrayList<>();

    public OrderDTO() {}

    public OrderDTO(Long orderId, String date, Double total) {
        this.orderId = orderId;
        this.date = date;
        this.total = total;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}
