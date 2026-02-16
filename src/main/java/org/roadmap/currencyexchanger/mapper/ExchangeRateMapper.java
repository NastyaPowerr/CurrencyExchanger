package org.roadmap.currencyexchanger.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.roadmap.currencyexchanger.dto.response.ExchangeRateResponseDto;
import org.roadmap.currencyexchanger.entity.ExchangeRate;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "baseCurrency", target = "baseCurrency")
    @Mapping(source = "targetCurrency", target = "targetCurrency")
    ExchangeRateResponseDto toResponseDto(ExchangeRate entity);
}
