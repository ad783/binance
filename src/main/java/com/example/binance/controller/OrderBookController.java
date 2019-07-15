package com.example.binance.controller;
/* Created by Ahmed Saifi on 13/07/19. */

import com.example.binance.entity.OrderBookUpdate;
import com.example.binance.model.OrderBookUpdateModel;
import com.example.binance.service.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-book")
public class OrderBookController {
    private final OrderBookService orderBookService;

    @Autowired
    public OrderBookController(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @RequestMapping(value = "/getOrderBook", method = RequestMethod.GET)
    public ResponseEntity<List<OrderBookUpdateModel>> getOrderBook(){
        return new ResponseEntity<>(orderBookService.getOrderBookModel(), HttpStatus.OK);
    }
}
