package com.example.binance.adapter;
/* Created by Ahmed Saifi on 14/07/19. */


import com.binance.api.client.domain.market.OrderBookEntry;
import com.example.binance.entity.OrderBookUpdate;
import com.example.binance.model.OrderBookUpdateModel;
import com.example.binance.model.PriceAndQuantity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;


public class BinanceAdapter {
    private static Gson gson = new Gson();
    public static NavigableMap<BigDecimal, BigDecimal> getMapFromOrderBookEntryList(List<OrderBookEntry> orderBookEntries){
        NavigableMap<BigDecimal, BigDecimal> map = new TreeMap<>(Comparator.reverseOrder());
        if(orderBookEntries == null){
            return map;
        }
        for (OrderBookEntry orderBookEntry : orderBookEntries) {
            map.put(new BigDecimal(orderBookEntry.getPrice()), new BigDecimal(orderBookEntry.getQty()));
        }
        return map;
    }

    public static List<OrderBookUpdateModel> toModelList(List<OrderBookUpdate> orderBookUpdateList){
        List<OrderBookUpdateModel> modelList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(orderBookUpdateList)){
            Type type = new TypeToken<Map<BigDecimal,BigDecimal>>() {
            }.getType();

            Type bestType = new TypeToken<PriceAndQuantity>() {
            }.getType();

            for (OrderBookUpdate orderBookUpdate : orderBookUpdateList) {
                OrderBookUpdateModel orderBookModel = new OrderBookUpdateModel();
                orderBookModel.setSymbol(orderBookUpdate.getSymbol());
                orderBookModel.setTime(orderBookUpdate.getTime().toEpochMilli());
                orderBookModel.setEventTime(orderBookUpdate.getEventTime());
                orderBookModel.setAsks(gson.fromJson(orderBookUpdate.getAsks(),type));
                orderBookModel.setBestAsk(gson.fromJson(orderBookUpdate.getBestAsk(),bestType));
                orderBookModel.setBids(gson.fromJson(orderBookUpdate.getBids(),type));
                orderBookModel.setBestBid(gson.fromJson(orderBookUpdate.getBestBid(),bestType));

                modelList.add(orderBookModel);
            }

        }
        return modelList;
    }

    public static long getBitCoinCount(OrderBookUpdateModel orderBook, boolean bid,double factor){
        double totalBitCoin = 0;
        double high,low;
        Map<BigDecimal, BigDecimal> map;
        if(bid) {
            high = orderBook.getBestBid().getPrice().doubleValue();
            low = high * factor;
            map = orderBook.getBids();
        }
        else{
            low = orderBook.getBestAsk().getPrice().doubleValue();
            high = low * factor;
            map = orderBook.getAsks();
        }

        for (Map.Entry<BigDecimal, BigDecimal> bidOrAsk : map.entrySet()) {
            double price = bidOrAsk.getKey().doubleValue();
            if (price >= low && price <= high) {
                totalBitCoin += price * bidOrAsk.getValue().doubleValue();
            }
        }
        return (long) totalBitCoin;
    }
}
