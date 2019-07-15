package com.example.binance.service.impl;
/* Created by Ahmed Saifi on 14/07/19. */


import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.example.binance.Constants;
import com.example.binance.adapter.BinanceAdapter;
import com.example.binance.repository.OrderBookUpdateRepository;
import com.example.binance.service.DepthCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class DepthCacheImpl implements DepthCache {
    private static final Logger LOG = LoggerFactory.getLogger(DepthCacheImpl.class);



    private Map<String, Long> symbolVsLastUpdateId = new HashMap<>();

    private Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>> depthCache = new ConcurrentHashMap<>();


    private final BinanceApiRestClient binanceApiRestClient;
    private final BinanceApiWebSocketClient binanceApiWebSocketClient;
    private final OrderBookUpdateRepository orderBookUpdateRepository;

    @Autowired
    public DepthCacheImpl(BinanceApiRestClient binanceApiRestClient,
                          BinanceApiWebSocketClient binanceApiWebSocketClient,
                          OrderBookUpdateRepository orderBookUpdateRepository) {
        this.binanceApiRestClient = binanceApiRestClient;
        this.binanceApiWebSocketClient = binanceApiWebSocketClient;
        this.orderBookUpdateRepository = orderBookUpdateRepository;
    }

    @Async
    @Override
    public void buildDepthCache(String symbol){
        LOG.info("Building Cache For Symbol {}",symbol);
        this.initializeDepthCache(symbol);
        this.startDepthEventStreaming(symbol);
    }


    private void initializeDepthCache(String symbol) {
        try {
            OrderBook orderBook = binanceApiRestClient.getOrderBook(symbol.toUpperCase(),
                    Constants.ORDER_BOOK_DEFAULT_LIMIT);

            this.symbolVsLastUpdateId.put(symbol, orderBook.getLastUpdateId());

            Map<String, NavigableMap<BigDecimal, BigDecimal>> symbolAskAndBids = new HashMap<>();

            symbolAskAndBids.put(Constants.ASKS, BinanceAdapter.getMapFromOrderBookEntryList(orderBook.getAsks()));
            symbolAskAndBids.put(Constants.BIDS,BinanceAdapter.getMapFromOrderBookEntryList(orderBook.getBids()));

            depthCache.put(symbol, symbolAskAndBids);

        }catch (Exception e){
            LOG.error("Exception caught while initializing depth cache for symbol {}", symbol);
        }
    }


   private void startDepthEventStreaming(String symbol) {
        binanceApiWebSocketClient.onDepthEvent(symbol.toLowerCase(), new BinanceApiCallback<DepthEvent>() {
            @Override
            public void onResponse(final DepthEvent response) {
                if (response.getUpdateId() > symbolVsLastUpdateId.get(symbol)) {
                    symbolVsLastUpdateId.put(symbol,response.getUpdateId());
                    updateOrderBook(getAsks(symbol), response.getAsks());
                    updateOrderBook(getBids(symbol), response.getBids());
                    saveDepthCache(symbol, response.getEventTime());
                }
            }

            @Override
            public void onFailure(final Throwable cause) {
                LOG.error("Depth Event Web Socket Failed For Symbol " + symbol);
                buildDepthCache(symbol);
            }
        });

    }


    private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries, List<OrderBookEntry> orderBookDeltas) {
        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                lastOrderBookEntries.remove(price);
            } else {
                lastOrderBookEntries.put(price, qty);
            }
        }
    }

    private void saveDepthCache(String symbol, Long eventTime) {
        orderBookUpdateRepository.createOrderBookUpdate(symbol,eventTime,getAsks(symbol),getBids(symbol));
    }

    @Override
    public NavigableMap<BigDecimal, BigDecimal> getAsks(String symbol) {
        return depthCache.get(symbol).get(Constants.ASKS);
    }

    @Override
    public NavigableMap<BigDecimal, BigDecimal> getBids(String symbol) {
        return depthCache.get(symbol).get(Constants.BIDS);
    }


    @Override
    public Map.Entry<BigDecimal, BigDecimal> getBestAsk(String symbol) {
        return getAsks(symbol).lastEntry();
    }

    @Override
    public Map.Entry<BigDecimal, BigDecimal> getBestBid(String symbol) {
        return getBids(symbol).firstEntry();
    }


    @Override
    public Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>> getDepthCache() {
        return depthCache;
    }
}
