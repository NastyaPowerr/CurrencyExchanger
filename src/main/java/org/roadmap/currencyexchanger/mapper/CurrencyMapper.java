package org.roadmap.currencyexchanger.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.roadmap.currencyexchanger.dto.request.CurrencyRequestDto;
import org.roadmap.currencyexchanger.dto.response.CurrencyResponseDto;
import org.roadmap.currencyexchanger.entity.CurrencyEntity;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    @Mapping(target = "id", ignore = true)
    CurrencyEntity toEntity(CurrencyRequestDto dto);

    CurrencyResponseDto toResponseDto(CurrencyEntity entity);
}
