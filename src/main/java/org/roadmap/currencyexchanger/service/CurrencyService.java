package org.roadmap.currencyexchanger.service;

import org.roadmap.currencyexchanger.dao.CurrencyDao;
import org.roadmap.currencyexchanger.entity.Currency;
import org.roadmap.currencyexchanger.mapper.CurrencyMapper;
import org.roadmap.currencyexchanger.dto.request.CurrencyRequestDto;
import org.roadmap.currencyexchanger.dto.response.CurrencyResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public CurrencyResponseDto save(CurrencyRequestDto dto) {
        Currency entity = CurrencyMapper.INSTANCE.toEntity(dto);
        Currency savedEntity = currencyDao.save(entity);
        return CurrencyMapper.INSTANCE.toResponseDto(savedEntity);
    }

    public CurrencyResponseDto getByCode(String code) {
        Currency entity = currencyDao.findByCode(code);
        return CurrencyMapper.INSTANCE.toResponseDto(entity);
    }

    public List<CurrencyResponseDto> getAll() {
        List<Currency> currencies = currencyDao.findAll();
        return currencies.stream()
                .map(CurrencyMapper.INSTANCE::toResponseDto)
                .collect(Collectors.toList());
    }
}