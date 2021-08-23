package com.hardware.store.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class NewsDTO {

    @NotEmpty
    private String news;
}
