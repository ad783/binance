package com.example.binance.service;
/* Created by Ahmed Saifi on 13/07/19. */


import com.example.binance.binance_enum.BinanceFixedPairElementEnum;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

public interface BinanceService {

    Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>> getDepthCache();

    Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache(String symbol);

    Set<String> getSymbols(BinanceFixedPairElementEnum fixedPairElement);

    Set<String> getAllSymbols();
}
