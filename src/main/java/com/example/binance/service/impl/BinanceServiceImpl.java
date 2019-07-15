package com.example.binance.service.impl;
/* Created by Ahmed Saifi on 13/07/19. */


import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import com.example.binance.binance_enum.BinanceFixedPairElementEnum;
import com.example.binance.service.BinanceService;
import com.example.binance.service.DepthCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;

@Service
public class BinanceServiceImpl implements BinanceService {

    private static final Logger LOG = LoggerFactory.getLogger(BinanceServiceImpl.class);
    private Map<BinanceFixedPairElementEnum,Set<String>> symbols = new HashMap<>();


    private final BinanceApiRestClient binanceApiRestClient;
    private final DepthCache depthCache;

    @Autowired
    public BinanceServiceImpl(BinanceApiRestClient binanceApiRestClient,
                              DepthCache depthCache) {
        this.binanceApiRestClient = binanceApiRestClient;
        this.depthCache = depthCache;
    }

    @PostConstruct
    public void init() {
        for (BinanceFixedPairElementEnum fixedPairElement : BinanceFixedPairElementEnum.values()) {
            LOG.info("Building Depth Cache For BinanceFixedPairElement {}",fixedPairElement);
            Set<String> symbols = getSymbolsBySecondElementOfPairFromAllPrices(fixedPairElement);
            symbols.forEach(depthCache::buildDepthCache); // this is an asynchronous call
        }
    }


    private Set<String> getSymbolsBySecondElementOfPairFromAllPrices(BinanceFixedPairElementEnum fixedPairElement){
        List<TickerPrice> prices = binanceApiRestClient.getAllPrices();

        prices.stream().filter(tickerPrice -> fixedPairElement.name().equals(tickerPrice.getSymbol()
                .substring(tickerPrice.getSymbol().length() - 3, tickerPrice.getSymbol().length())))
                .forEach(tickerPrice -> {
                    symbols.computeIfAbsent(fixedPairElement,k-> new TreeSet<>());
                    symbols.get(fixedPairElement).add(tickerPrice.getSymbol());
                });

        return symbols.get(fixedPairElement);
    }


    @Override
    public Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>> getDepthCache() {
        return depthCache.getDepthCache();
    }

    @Override
    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache(String symbol) {
        Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>> depthCache = this.depthCache.getDepthCache();
        return depthCache.get(symbol);
    }

    @Override
    public Set<String> getSymbols(BinanceFixedPairElementEnum fixedPairElement) {
        return symbols.get(fixedPairElement);
    }

    @Override
    public Set<String> getAllSymbols() {
        Set<String> allSymbols = new TreeSet<>();
        Collection<Set<String>> values = symbols.values();
        for (Set<String> value : values) {
            allSymbols.addAll(value);
        }
        return allSymbols;
    }
}
