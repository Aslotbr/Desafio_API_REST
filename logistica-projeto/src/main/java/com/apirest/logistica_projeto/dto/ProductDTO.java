package com.apirest.logistica_projeto.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDTO {
@JsonProperty("product_id")
private Long productId;
@JsonIgnore
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

@JsonIgnore
public Double getRawValue() {
    return value;
}

public void setRawValue(Double value) {
    this.value = value;
}

@JsonProperty("value")
public String getValue() {
    return String.format("%.2f", value);
}

public void setValue(String value) {
    this.value = Double.parseDouble(value.replace(",", "."));
}
}