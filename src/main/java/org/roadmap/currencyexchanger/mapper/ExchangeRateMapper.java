package org.roadmap.currencyexchanger.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.roadmap.currencyexchanger.model.dto.response.ExchangeRateResponseDto;
import org.roadmap.currencyexchanger.model.entity.ExchangeRateEntity;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "baseCurrencyEntity", target = "baseCurrency")
    @Mapping(source = "targetCurrencyEntity", target = "targetCurrency")
    ExchangeRateResponseDto toResponseDto(ExchangeRateEntity entity);
}
