package org.roadmap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.roadmap.model.dto.request.CurrencyRequestDto;
import org.roadmap.model.dto.response.CurrencyResponseDto;
import org.roadmap.model.entity.CurrencyEntity;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(target = "id", ignore = true)
    CurrencyEntity toEntity(CurrencyRequestDto dto);

    CurrencyResponseDto toResponseDto(CurrencyEntity entity);
}
