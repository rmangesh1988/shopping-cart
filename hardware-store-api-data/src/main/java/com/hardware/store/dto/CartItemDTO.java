package com.hardware.store.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDTO {

    private Long productId;

    private Integer quantity;

}
