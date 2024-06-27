package com.foreign.exchange.bank.service;

import com.foreign.exchange.bank.dto.ExchangeRate;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateService {


    ExchangeRate getExchangeRate(String targetCurrency);

    List<ExchangeRate> getLatestExchangeRates(String targetCurrency);
}
