package com.foreign.exchange.bank.controller;


import com.foreign.exchange.bank.dto.ExchangeRate;
import com.foreign.exchange.bank.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fx")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    /**
     * Retrieves foreign exchange rates from USD to specified target currency.
     * If no target currency is specified, returns rates for EUR, GBP, JPY, CZK.
     *
     * @param targetCurrency (optional) Target currency code (e.g., EUR, GBP, JPY, CZK)
     * @return ResponseEntity with list of ExchangeRate objects containing rate information
     */
    @GetMapping
    public ExchangeRate getExchangeRate(@RequestParam String targetCurrency) {
        return exchangeRateService.getExchangeRate(targetCurrency);
    }

    /**
     * Retrieves a time series of exchange rates from USD to the specified target currency.
     * Returns the latest 3 rates with a step of 1 day.
     *
     * @param targetCurrency Target currency code (e.g., EUR, GBP, JPY, CZK)
     * @return ResponseEntity with list of ExchangeRate objects representing time series data
     */
    @GetMapping("/{targetCurrency}")
    public List<ExchangeRate> getLatestExchangeRates(@PathVariable String targetCurrency) {
        return exchangeRateService.getLatestExchangeRates(targetCurrency);
    }
}
