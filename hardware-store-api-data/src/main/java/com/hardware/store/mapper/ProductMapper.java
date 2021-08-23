package com.hardware.store.mapper;

import com.hardware.store.domain.Product;
import com.hardware.store.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductMapper {

    void fromDTO(ProductDTO productDTO, @MappingTarget Product product);

    Product fromDTO(ProductDTO productDTO);
}
