package com.example.binance.service;
/* Created by Ahmed Saifi on 13/07/19. */


import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.Order;
import com.example.binance.model.OrderBookUpdateModel;
import org.springframework.scheduling.annotation.Async;

public interface BuyAndSellService {
    NewOrderResponse newOrderAction(String symbol, String buyingPrice, OrderSide orderSide, OrderType orderType, String quantity);

    Order getOrderStatus(NewOrderResponse sellOrderResponse);

    @Async
    void check10sPostConditionsAndBuy(OrderBookUpdateModel orderBook);

    void buy(OrderBookUpdateModel orderBookModel);
}
