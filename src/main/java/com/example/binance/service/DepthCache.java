package com.example.binance.service;
/* Created by Ahmed Saifi on 14/07/19. */


import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

public interface DepthCache {
    @Async
    void buildDepthCache(String symbol);

    NavigableMap<BigDecimal, BigDecimal> getAsks(String symbol);

    NavigableMap<BigDecimal, BigDecimal> getBids(String symbol);

    Map.Entry<BigDecimal, BigDecimal> getBestAsk(String symbol);

    Map.Entry<BigDecimal, BigDecimal> getBestBid(String symbol);

    Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>> getDepthCache();
}
