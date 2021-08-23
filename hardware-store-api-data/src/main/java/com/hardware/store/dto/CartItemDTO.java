package com.hardware.store.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CartItemDTO {

    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1, message = "Quantity should be atleast 1")
    private Integer quantity;

}
