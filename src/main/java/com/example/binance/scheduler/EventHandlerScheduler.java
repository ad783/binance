package com.example.binance.scheduler;
/* Created by Ahmed Saifi on 14/07/19. */

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import com.example.binance.binance_enum.BinanceFixedPairElementEnum;
import com.example.binance.event_handler.OrderBookEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventHandlerScheduler {

    private final OrderBookEventHandler orderBookEventHandler;
    private final BinanceApiRestClient binanceApiRestClient;

    @Autowired
    public EventHandlerScheduler(OrderBookEventHandler orderBookEventHandler,
                                 BinanceApiRestClient binanceApiRestClient) {
        this.orderBookEventHandler = orderBookEventHandler;
        this.binanceApiRestClient = binanceApiRestClient;
    }

    @Scheduled(fixedDelay=60000)
    public void updateBTCSymbolPrices(){
        TickerStatistics btcusdtPrice = binanceApiRestClient.get24HrPriceStatistics("BTCUSDT");
        orderBookEventHandler.updateBTCUSDTPrice(btcusdtPrice);

        List<TickerPrice> tickerPrices = binanceApiRestClient.getAllPrices();
        for (TickerPrice tickerPrice: tickerPrices){

            String substring = tickerPrice.getSymbol().substring(tickerPrice.getSymbol().length() - 3, tickerPrice.getSymbol().length());
            if (BinanceFixedPairElementEnum.BTC.name().equals(substring)) {
                orderBookEventHandler.addTickerPrice(tickerPrice);
            }
        }
    }
}
