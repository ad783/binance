package com.example.binance.controller;
/* Created by Ahmed Saifi on 13/07/19. */


import com.example.binance.binance_enum.BinanceFixedPairElementEnum;
import com.example.binance.service.BinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/binance")
public class BinanceController {

    private final BinanceService binanceApiService;

    @Autowired
    public BinanceController(BinanceService binanceApiService) {
        this.binanceApiService = binanceApiService;
    }


    @RequestMapping(value = "/getSymbols", method = RequestMethod.GET)
    public ResponseEntity<Set<String>> getSymbols(@RequestParam(value = "fixedPairElement")BinanceFixedPairElementEnum fixedPairElement){
        return new ResponseEntity<>(binanceApiService.getSymbols(fixedPairElement), HttpStatus.OK);
    }

    @RequestMapping(value = "/getAllSymbols", method = RequestMethod.GET)
    public ResponseEntity<Set<String>> getSymbols(){
        return new ResponseEntity<>(binanceApiService.getAllSymbols(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getAllDepthCache", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>>> getAllDepthCache(){
        return new ResponseEntity<>(binanceApiService.getDepthCache(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getDepthCache", method = RequestMethod.GET)
    public ResponseEntity<Map<String, NavigableMap<BigDecimal, BigDecimal>>> getAllDepthCache(@RequestParam(value = "symbol") String symbol){
        return new ResponseEntity<>(binanceApiService.getDepthCache(symbol), HttpStatus.OK);
    }
}
