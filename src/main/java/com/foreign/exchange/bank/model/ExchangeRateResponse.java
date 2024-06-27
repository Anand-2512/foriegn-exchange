package com.foreign.exchange.bank.model;

import java.time.LocalDate;
import java.util.Map;

public class ExchangeRateResponse {
    private LocalDate date;
    private Map<String, Double> rates;

    // Getters and setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
