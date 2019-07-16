package com.example.binance.scheduler;
/* Created by Ahmed Saifi on 13/07/19. */

import com.example.binance.service.BinanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NavigableMap;

@Component
public class UpdateWebInfoScheduler {
    private final SimpMessagingTemplate messageTemplate;
    private final BinanceService binanceApiService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UpdateWebInfoScheduler(SimpMessagingTemplate messageTemplate,
                                  BinanceService binanceApiService,
                                  ObjectMapper objectMapper) {
        this.messageTemplate = messageTemplate;
        this.binanceApiService = binanceApiService;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay=1000)
    public void updateAskAndBids() throws Exception {
        Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>> depthCache = binanceApiService.getDepthCache();
        this.messageTemplate.convertAndSend("/asks-bids/price-quantity", objectMapper.writeValueAsString(depthCache));
    }
}
