package com.hardware.store.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ProductDTO {

    @NotEmpty
    private String name;

    @NotNull
    @Min(value = 1, message = "Product price has to be greater than zero!")
    private Double price;
}
