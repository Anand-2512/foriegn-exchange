package com.foreign.exchange.bank.controller;


import com.foreign.exchange.bank.dto.ExchangeRate;
import com.foreign.exchange.bank.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Test
    public void testGetExchangeRate() throws Exception {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setDate(LocalDate.now());
        exchangeRate.setSourceCurrency("USD");
        exchangeRate.setTargetCurrency("EUR");
        exchangeRate.setExchangeRate(0.85);

        Mockito.when(exchangeRateService.getExchangeRate(anyString())).thenReturn(exchangeRate);

        mockMvc.perform(get("/fx")
                        .param("targetCurrency", "EUR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceCurrency").value("USD"))
                .andExpect(jsonPath("$.targetCurrency").value("EUR"))
                .andExpect(jsonPath("$.rate").value(0.85));
    }

    @Test
    public void testGetLatestExchangeRates() throws Exception {
        ExchangeRate exchangeRate1 = new ExchangeRate();
        exchangeRate1.setDate(LocalDate.now().minusDays(2));
        exchangeRate1.setSourceCurrency("USD");
        exchangeRate1.setTargetCurrency("EUR");
        exchangeRate1.setExchangeRate(0.85);

        ExchangeRate exchangeRate2 = new ExchangeRate();
        exchangeRate2.setDate(LocalDate.now().minusDays(1));
        exchangeRate2.setSourceCurrency("USD");
        exchangeRate2.setTargetCurrency("EUR");
        exchangeRate2.setExchangeRate(0.86);

        ExchangeRate exchangeRate3 = new ExchangeRate();
        exchangeRate3.setDate(LocalDate.now());
        exchangeRate3.setSourceCurrency("USD");
        exchangeRate3.setTargetCurrency("EUR");
        exchangeRate3.setExchangeRate(0.87);

        List<ExchangeRate> exchangeRates = Arrays.asList(exchangeRate1, exchangeRate2, exchangeRate3);

        Mockito.when(exchangeRateService.getLatestExchangeRates(anyString())).thenReturn(exchangeRates);

        mockMvc.perform(get("/fx/EUR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sourceCurrency").value("USD"))
                .andExpect(jsonPath("$[0].targetCurrency").value("EUR"))
                .andExpect(jsonPath("$[0].rate").value(0.85))
                .andExpect(jsonPath("$[1].sourceCurrency").value("USD"))
                .andExpect(jsonPath("$[1].targetCurrency").value("EUR"))
                .andExpect(jsonPath("$[1].rate").value(0.86))
                .andExpect(jsonPath("$[2].sourceCurrency").value("USD"))
                .andExpect(jsonPath("$[2].targetCurrency").value("EUR"))
                .andExpect(jsonPath("$[2].rate").value(0.87));
    }
}
