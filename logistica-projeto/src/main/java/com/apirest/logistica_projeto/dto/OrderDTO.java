package com.apirest.logistica_projeto.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDTO {
    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("date")
    private String date;

    // Campo usado internamente, não será serializado diretamente
    @JsonIgnore
    private Double total = 0.0;

    @JsonProperty("total")
    private String totalFormatted;

    @JsonProperty("products")
    private List<ProductDTO> products = new ArrayList<>();

    public OrderDTO() {}

    public OrderDTO(Long orderId, String date) {
        this.orderId = orderId;
        this.date = date;
        this.total = 0.0;
        this.totalFormatted = "0.00";
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

    @JsonIgnore
    public Double getTotal() {
        return total;
    }

    @JsonProperty("total")
    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotal(Double total) {
        this.total = total;
        // Força o formato com ponto no decimal
        this.totalFormatted = String.format(Locale.US, "%.2f", total);
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}
