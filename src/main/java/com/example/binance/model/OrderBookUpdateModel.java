package com.example.binance.model;
/* Created by Ahmed Saifi on 13/07/19. */


import java.math.BigDecimal;
import java.util.Map;

public class OrderBookUpdateModel {
    private Long time;
    private Long eventTime;
    private String symbol;
    private Map<BigDecimal,BigDecimal> asks;
    private Map<BigDecimal,BigDecimal> bids;
    private PriceAndQuantity bestAsk;
    private PriceAndQuantity bestBid;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Map<BigDecimal, BigDecimal> getAsks() {
        return asks;
    }

    public void setAsks(Map<BigDecimal, BigDecimal> asks) {
        this.asks = asks;
    }

    public Map<BigDecimal, BigDecimal> getBids() {
        return bids;
    }

    public void setBids(Map<BigDecimal, BigDecimal> bids) {
        this.bids = bids;
    }

    public PriceAndQuantity getBestAsk() {
        return bestAsk;
    }

    public void setBestAsk(PriceAndQuantity bestAsk) {
        this.bestAsk = bestAsk;
    }

    public PriceAndQuantity getBestBid() {
        return bestBid;
    }

    public void setBestBid(PriceAndQuantity bestBid) {
        this.bestBid = bestBid;
    }
}
