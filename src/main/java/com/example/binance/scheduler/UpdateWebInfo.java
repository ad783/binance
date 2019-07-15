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
public class UpdateWebInfo {

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @Autowired
    private BinanceService binanceApiService;

    @Scheduled(fixedDelay=1000)
    public void priceManualConvert() throws Exception {
        Map<String, Map<String, NavigableMap<BigDecimal, BigDecimal>>> depthCache = binanceApiService.getDepthCache();
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(depthCache);
        this.messageTemplate.convertAndSend("/stock/price", jsonInString);
    }
}
