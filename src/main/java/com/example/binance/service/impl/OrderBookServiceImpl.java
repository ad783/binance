package com.example.binance.service.impl;
/* Created by Ahmed Saifi on 13/07/19. */


import com.example.binance.adapter.BinanceAdapter;
import com.example.binance.model.OrderBookUpdateModel;
import com.example.binance.repository.OrderBookUpdateRepository;
import com.example.binance.entity.OrderBookUpdate;
import com.example.binance.service.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderBookServiceImpl implements OrderBookService {
    private final OrderBookUpdateRepository orderBookUpdateRepository;

    @Autowired
    public OrderBookServiceImpl(OrderBookUpdateRepository orderBookDao) {
        this.orderBookUpdateRepository = orderBookDao;
    }

    @Override
    public List<OrderBookUpdate> getOrderBook(){
        return orderBookUpdateRepository.getOrderBookUpdate();
    }

    @Override
    public List<OrderBookUpdateModel> getOrderBookModel(){
        return BinanceAdapter.toModelList(orderBookUpdateRepository.getOrderBookUpdate());
    }

    @Override
    public List<OrderBookUpdateModel> getOrderBookModel(String symbol){
        return BinanceAdapter.toModelList(orderBookUpdateRepository.getOrderBookUpdate(symbol));
    }
}
