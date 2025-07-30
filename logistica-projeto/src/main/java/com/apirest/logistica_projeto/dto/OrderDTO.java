package com.apirest.logistica_projeto.dto;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private Long orderId;
    private String date;
    private transient Double total = 0.0; // mantemos o valor numérico para cálculo
    private String totalFormatted; // usado para exibição
    private List<ProductDTO> products = new ArrayList<>();

    public OrderDTO() {}

    public OrderDTO(Long orderId, String date, Double total) {
        this.orderId = orderId;
        this.date = date;
        setTotal(total);
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

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public void setTotal(Double total) {
        this.total = total;
        DecimalFormat df = new DecimalFormat("0.00");
        this.totalFormatted = df.format(total);
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}
