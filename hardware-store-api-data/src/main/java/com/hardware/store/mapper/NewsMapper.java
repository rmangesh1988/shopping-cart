package com.hardware.store.mapper;

import com.hardware.store.domain.News;
import com.hardware.store.dto.NewsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface NewsMapper {

    void fromDTO(NewsDTO newsDTO, @MappingTarget News news);

    News fromDTO(NewsDTO newsDTO);
}
