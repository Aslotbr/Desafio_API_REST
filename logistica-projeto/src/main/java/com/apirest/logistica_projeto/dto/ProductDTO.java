package com.apirest.logistica_projeto.dto;

public class ProductDTO {
    private Long productId;
    private Double value;

    public ProductDTO() {}

    public ProductDTO(Long productId, Double value) {
        this.productId = productId;
        this.value = value;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
