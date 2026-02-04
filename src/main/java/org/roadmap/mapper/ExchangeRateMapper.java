package org.roadmap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.roadmap.model.dto.response.ExchangeRateResponseDto;
import org.roadmap.model.entity.ExchangeRateEntity;

@Mapper(uses = CurrencyMapper.class)
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "baseCurrencyEntity", target = "baseCurrency")
    @Mapping(source = "targetCurrencyEntity", target = "targetCurrency")
    ExchangeRateResponseDto toResponseDto(ExchangeRateEntity entity);
}
