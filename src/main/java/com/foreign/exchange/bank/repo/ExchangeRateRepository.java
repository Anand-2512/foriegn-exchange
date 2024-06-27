package com.foreign.exchange.bank.repo;


import com.foreign.exchange.bank.dto.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    

    List<ExchangeRate> findTop3ByTargetCurrencyOrderByDateDesc(String targetCurrency);

    boolean existsByDateAndTargetCurrency(LocalDate today, String targetCurrency);
}

