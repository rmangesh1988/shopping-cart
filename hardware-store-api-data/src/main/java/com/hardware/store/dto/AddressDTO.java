package com.hardware.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    @NotEmpty
    private String building;

    @NotEmpty
    private String street;

    @NotEmpty
    private String city;

    @NotNull
    private Integer zipCode;
}
