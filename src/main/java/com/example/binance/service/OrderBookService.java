package com.example.binance.service;
/* Created by Ahmed Saifi on 13/07/19. */


import com.example.binance.entity.OrderBookUpdate;
import com.example.binance.model.OrderBookUpdateModel;

import java.util.List;

public interface OrderBookService {
    List<OrderBookUpdate> getOrderBook();

    List<OrderBookUpdateModel> getOrderBookModel();

    List<OrderBookUpdateModel> getOrderBookModel(String symbol);
}
