package com.hardware.store.mapper;

import com.hardware.store.domain.Address;
import com.hardware.store.dto.AddressDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AddressMapper {

    Address fromDTO(AddressDTO addressDTO);
}
