package com.example.binance.repository;
/* Created by Ahmed Saifi on 13/07/19. */

import com.example.binance.entity.OrderBookUpdate;
import com.example.binance.model.PriceQuantity;
import com.google.gson.Gson;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.TimeUnit;

@Repository
public class OrderBookUpdateRepository {
    private final InfluxDB influxDB;
    private final InfluxDBResultMapper influxDBResultMapper;
    private final Gson gson;

    @Autowired
    public OrderBookUpdateRepository(InfluxDB influxDB, InfluxDBResultMapper influxDBResultMapper, Gson gson) {
        this.influxDB = influxDB;
        this.influxDBResultMapper = influxDBResultMapper;
        this.gson = gson;
    }


    public List<OrderBookUpdate> getOrderBookUpdate(){
        Query query = new Query("SELECT * FROM order_book_update", "binance");
        QueryResult queryResult = influxDB.query(query);
        return  influxDBResultMapper.toPOJO(queryResult, OrderBookUpdate.class);
    }

    public List<OrderBookUpdate> getOrderBookUpdate(String symbol){
        Query query = new Query("SELECT * FROM order_book_update where symbol ='" + symbol + "'", "binance");
        QueryResult queryResult = influxDB.query(query);
        return  influxDBResultMapper.toPOJO(queryResult, OrderBookUpdate.class);
    }


    public void createOrderBookUpdate(String symbol, Long eventTime,
                               NavigableMap<BigDecimal, BigDecimal> asks,
                               NavigableMap<BigDecimal, BigDecimal> bids) {
        Point point = Point.measurement("order_book_update")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("event_time", eventTime)
                .addField("symbol", symbol)
                .addField("asks", gson.toJson(asks))
                .addField("bids", gson.toJson(bids))
                .addField("best_ask", gson.toJson(new PriceQuantity(asks.lastEntry().getKey(),asks.lastEntry().getValue())))
                .addField("best_bid", gson.toJson(new PriceQuantity(bids.firstEntry().getKey(),bids.firstEntry().getValue())))
                .build();
        influxDB.write(point);
    }
}
