package com.example.binance.service.impl;
/* Created by Ahmed Saifi on 13/07/19. */


import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.example.binance.Constants;
import com.example.binance.adapter.BinanceAdapter;
import com.example.binance.model.OrderBookUpdateModel;
import com.example.binance.service.BuyAndSellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BuyAndSellServiceImpl implements BuyAndSellService {


    private static final Logger LOG = LoggerFactory.getLogger(BuyAndSellServiceImpl.class);
    private Map<String, Long> symbolVs10sWindowThresholdTime = new HashMap<>();

    private final BinanceApiRestClient binanceApiRestClient;

    @Autowired
    public BuyAndSellServiceImpl(BinanceApiRestClient binanceApiRestClient) {
        this.binanceApiRestClient = binanceApiRestClient;
    }

    @Override
    public NewOrderResponse newOrderAction(String symbol, String buyingPrice, OrderSide orderSide, OrderType orderType, String quantity){
        return binanceApiRestClient.newOrder(new NewOrder(symbol, orderSide, orderType, TimeInForce.GTC, quantity, buyingPrice));
    }

    @Override
    public Order getOrderStatus(NewOrderResponse order){
        return binanceApiRestClient.getOrderStatus(new OrderStatusRequest(order.getClientOrderId(), order.getOrderId()));
    }

    @Async
    @Override
    public void check10sPostConditionsAndBuy(OrderBookUpdateModel orderBook){

        long bitCoinCountBid = BinanceAdapter.getBitCoinCount(orderBook,true,Constants.BID_FACTOR);
        long bitCoinCountAsk = BinanceAdapter.getBitCoinCount(orderBook,false,Constants.ASK_FACTOR);


        if (bitCoinCountBid >= 3 && bitCoinCountBid >= 4 * bitCoinCountAsk) {
            Long thresholdTime = symbolVs10sWindowThresholdTime.get(orderBook.getSymbol());
            if(thresholdTime == null){
                symbolVs10sWindowThresholdTime.put(orderBook.getSymbol(),System.currentTimeMillis() + Constants.TEN_SECS);
            }
            else {
                if(System.currentTimeMillis() >= thresholdTime) {
                    this.buy(orderBook); // all conditions are satisfied so now we can buy
                }
            }
        }
        else {
            symbolVs10sWindowThresholdTime.put(orderBook.getSymbol(),null);
        }
    }

    @Override
    public void buy(OrderBookUpdateModel orderBookModel) {
        String symbol = orderBookModel.getSymbol();
        double buyingPrice = ((orderBookModel.getBestAsk().getPrice().doubleValue() +
                orderBookModel.getBestBid().getPrice().doubleValue())/2) * 1.005; // +0.5% of avg of best ask and bid

        this.newOrderAction(symbol, String.valueOf(buyingPrice), OrderSide.BUY, OrderType.LIMIT, "10");

        double stopLossPrice = buyingPrice - 0.07 * buyingPrice;

        this.newOrderAction(symbol, String.valueOf(stopLossPrice), OrderSide.SELL, OrderType.STOP_LOSS, "10");

        double sellingPrice = buyingPrice - buyingPrice * 0.05;

        NewOrderResponse sellOrderResponse = this.newOrderAction(symbol, String.valueOf(sellingPrice), OrderSide.SELL, OrderType.LIMIT, "10");

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            LOG.error("InterruptedException caught while sleeping for 20s " + e);
        }

        Order orderStatus = this.getOrderStatus(sellOrderResponse);

        if (orderStatus.getStatus().equals(OrderStatus.FILLED))
            return;

        double reducedSellPrice = sellingPrice - sellingPrice * 0.01;
        this.newOrderAction(symbol, String.valueOf(reducedSellPrice), OrderSide.SELL, OrderType.LIMIT, "10");
    }
}
