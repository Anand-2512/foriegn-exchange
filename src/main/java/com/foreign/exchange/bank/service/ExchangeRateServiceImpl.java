package com.foreign.exchange.bank.service;

import com.foreign.exchange.bank.dto.ExchangeRate;
import com.foreign.exchange.bank.model.ExchangeRateResponse;
import com.foreign.exchange.bank.repo.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    RestTemplate restTemplate;

    @Value("${exchange.rate.api.latest}")
    private String latestExchangeRateUrl;

    @Value("${exchange.rate.api.specific}")
    private String baseExchangeRateUrl;

    @Override
    public ExchangeRate getExchangeRate(String targetCurrency) {
        LocalDate today = LocalDate.now();
        if (!exchangeRateRepository.existsByDateAndTargetCurrency(today, targetCurrency)) {
            fetchAndStoreExchangeRate(targetCurrency, today);
        }
        return exchangeRateRepository.findTop3ByTargetCurrencyOrderByDateDesc(targetCurrency).get(0);
    }


    @Override
    public List<ExchangeRate> getLatestExchangeRates(String targetCurrency) {
        LocalDate today = LocalDate.now();
        if (!exchangeRateRepository.existsByDateAndTargetCurrency(today, targetCurrency)) {
            fetchAndStoreLatestExchangeRate();
        }
        return exchangeRateRepository.findTop3ByTargetCurrencyOrderByDateDesc(targetCurrency);
    }

    private void fetchAndStoreLatestExchangeRate() {
        ExchangeRateResponse response = restTemplate.getForObject(latestExchangeRateUrl, ExchangeRateResponse.class);
        if (response != null && response.getRates() != null) {
            response.getRates().forEach((currency, rate) -> {
                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setDate(response.getDate());
                exchangeRate.setSourceCurrency("USD");
                exchangeRate.setTargetCurrency(currency);
                exchangeRate.setExchangeRate(rate);
                exchangeRateRepository.save(exchangeRate);
            });
        }
    }

    private void fetchAndStoreExchangeRate(String targetCurrency, LocalDate date) {
        String url = UriComponentsBuilder.fromHttpUrl(baseExchangeRateUrl)
                .pathSegment(date.toString())
                .queryParam("to", targetCurrency)
                .toUriString();
        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
        if (response != null && response.getRates().containsKey(targetCurrency)) {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setDate(response.getDate());
            exchangeRate.setSourceCurrency("USD");
            exchangeRate.setTargetCurrency(targetCurrency);
            exchangeRate.setExchangeRate(response.getRates().get(targetCurrency));
            exchangeRateRepository.save(exchangeRate);
        }
    }
}
