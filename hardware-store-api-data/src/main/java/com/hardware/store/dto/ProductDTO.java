package com.hardware.store.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ProductDTO {

    @NotEmpty
    private String name;

    @NotNull
    private Double price;
}
