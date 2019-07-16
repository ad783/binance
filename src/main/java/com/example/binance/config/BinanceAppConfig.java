package com.example.binance.config;
/* Created by Ahmed Saifi on 13/07/19. */

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BinanceAppConfig {
    // replace API-KEY and SECRET by actual values
    private final BinanceApiClientFactory binanceApiClientFactory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");

    @Bean
    public BinanceApiRestClient binanceApiRestClient(){
        return binanceApiClientFactory.newRestClient();
    }

    @Bean
    public BinanceApiWebSocketClient binanceApiWebSocketClient(){
        return binanceApiClientFactory.newWebSocketClient();
    }

    @Bean
    public BinanceApiAsyncRestClient binanceApiAsyncRestClient(){
        return binanceApiClientFactory.newAsyncRestClient();
    }

    @Bean
    public InfluxDBResultMapper influxDBResultMapper(){
        return new InfluxDBResultMapper();
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
