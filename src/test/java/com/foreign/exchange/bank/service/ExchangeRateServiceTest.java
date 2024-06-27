package com.foreign.exchange.bank.service;


import com.foreign.exchange.bank.dto.ExchangeRate;
import com.foreign.exchange.bank.model.ExchangeRateResponse;
import com.foreign.exchange.bank.repo.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;

@SpringBootTest
public class ExchangeRateServiceTest {

    @MockBean
    private ExchangeRateRepository exchangeRateRepository;

    @MockBean
     RestTemplate restTemplate;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetExchangeRateFromRepository() {
        ExchangeRate existingRate = new ExchangeRate();
        existingRate.setDate(LocalDate.now());
        existingRate.setSourceCurrency("USD");
        existingRate.setTargetCurrency("EUR");
        existingRate.setExchangeRate(0.85);

        Mockito.when(exchangeRateRepository.existsByDateAndTargetCurrency(any(LocalDate.class), anyString())).thenReturn(true);
        Mockito.when(exchangeRateRepository.findTop3ByTargetCurrencyOrderByDateDesc(anyString()))
                .thenReturn(Arrays.asList(existingRate));

        ExchangeRate result = exchangeRateService.getExchangeRate("EUR");

        assertEquals(existingRate, result);
    }

    @Test
    public void testGetExchangeRateFromExternalService() {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setDate(LocalDate.now());
        Map<String,Double> map = new HashMap();
        map.put(   "EUR", 0.85);
        map.put(   "GBP", 0.75);
        map.put(   "JPY", 110.5);
        mockResponse.setRates(map);

        Mockito.when(exchangeRateRepository.existsByDateAndTargetCurrency(any(LocalDate.class), anyString())).thenReturn(false);
        Mockito.when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);

        exchangeRateService.getExchangeRate("EUR");

        Mockito.verify(exchangeRateRepository, times(1)).save(any(ExchangeRate.class));
    }

    @Test
    public void testGetLatestExchangeRatesFromRepository() {
        ExchangeRate rate1 = new ExchangeRate();
        rate1.setDate(LocalDate.now().minusDays(2));
        rate1.setSourceCurrency("USD");
        rate1.setTargetCurrency("EUR");
        rate1.setExchangeRate(0.85);

        ExchangeRate rate2 = new ExchangeRate();
        rate2.setDate(LocalDate.now().minusDays(1));
        rate2.setSourceCurrency("USD");
        rate2.setTargetCurrency("EUR");
        rate2.setExchangeRate(0.86);

        ExchangeRate rate3 = new ExchangeRate();
        rate3.setDate(LocalDate.now());
        rate3.setSourceCurrency("USD");
        rate3.setTargetCurrency("EUR");
        rate3.setExchangeRate(0.87);

        Mockito.when(exchangeRateRepository.existsByDateAndTargetCurrency(any(LocalDate.class), anyString())).thenReturn(true);
        Mockito.when(exchangeRateRepository.findTop3ByTargetCurrencyOrderByDateDesc(anyString()))
                .thenReturn(Arrays.asList(rate1, rate2, rate3));

        List<ExchangeRate> result = exchangeRateService.getLatestExchangeRates("EUR");

        assertEquals(3, result.size());
        assertEquals(rate1, result.get(0));
        assertEquals(rate2, result.get(1));
        assertEquals(rate3, result.get(2));
    }

    @Test
    public void testGetLatestExchangeRatesFromExternalService() {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setDate(LocalDate.now());
        Map<String,Double> map = new HashMap();
        map.put(   "EUR", 0.85);
        map.put(   "GBP", 0.75);
        map.put(   "JPY", 110.5);
        mockResponse.setRates(map);

        Mockito.when(exchangeRateRepository.existsByDateAndTargetCurrency(any(LocalDate.class), anyString())).thenReturn(false);
        Mockito.when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);

        exchangeRateService.getLatestExchangeRates("EUR");

        Mockito.verify(exchangeRateRepository, times(3)).save(any(ExchangeRate.class));
    }
}
