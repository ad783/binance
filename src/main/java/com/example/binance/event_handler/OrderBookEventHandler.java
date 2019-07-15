package com.example.binance.event_handler;
/* Created by Ahmed Saifi on 13/07/19. */

import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import com.example.binance.Constants;
import com.example.binance.binance_enum.BinanceFixedPairElementEnum;
import com.example.binance.model.OrderBookUpdateModel;
import com.example.binance.service.BinanceService;
import com.example.binance.service.BuyAndSellService;
import com.example.binance.service.OrderBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class OrderBookEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OrderBookEventHandler.class);

    private Map<String, BlockingQueue<OrderBookUpdateModel>> symbolVsOrderQueue = new HashMap<>();

    private Map<String, Deque<TickerPrice>> symbolVsTicketPrices = new HashMap<>();
    private TickerStatistics btcusdtPrice;

    private final BinanceService binanceApiService;
    private final BuyAndSellService buyAndSellService;
    private final OrderBookService orderBookService;

    @Autowired
    public OrderBookEventHandler(BinanceService binanceApiService,
                                 BuyAndSellService buyAndSellService,
                                 OrderBookService orderBookService) {
        this.binanceApiService = binanceApiService;
        this.buyAndSellService = buyAndSellService;
        this.orderBookService = orderBookService;
    }

    @PostConstruct
    public void init() {
        Set<String> symbols = binanceApiService.getSymbols(BinanceFixedPairElementEnum.BTC);
        symbols.forEach(symbol -> {
            symbolVsOrderQueue.put(symbol,new ArrayBlockingQueue<>(Constants.SYMBOL_BLOCKING_QUEUE_SIZE, true));

            new Thread(new AsyncEventHandler(symbol)).start();
            new Thread(new FeedHistoryDataAsRealTimeData(symbol)).start();

        });
    }

    class AsyncEventHandler implements Runnable{
        private String symbol;

        AsyncEventHandler(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public void run() {
            BlockingQueue<OrderBookUpdateModel> queue = symbolVsOrderQueue.get(symbol);
            while (true){
                try {
                    OrderBookUpdateModel orderBook = queue.take();
                    String symbol = orderBook.getSymbol();

                    double priceChangePercentBTC = Double.parseDouble(btcusdtPrice.getPriceChangePercent());


                    Deque<TickerPrice> tickerPriceDeque = symbolVsTicketPrices.get(symbol);
                    if (tickerPriceDeque == null){
                        continue;
                    }

                    double priceChangePercentage = (Double.valueOf(tickerPriceDeque.getLast().getPrice()) -
                                Double.valueOf(tickerPriceDeque.getFirst().getPrice())) /
                            Double.valueOf(tickerPriceDeque.getFirst().getPrice()) * 100;

                    // Initial condition's check
                    if (Math.abs(priceChangePercentBTC) < 3 && Math.abs(priceChangePercentage) < 1.5) {
                        buyAndSellService.check10sPostConditionsAndBuy(orderBook);
                    }
                } catch (InterruptedException e) {
                    LOG.error("Exception caught while buying {}" + e.toString());
                }
            }
        }
    }

    class FeedHistoryDataAsRealTimeData implements Runnable{
        private String symbol;

        FeedHistoryDataAsRealTimeData(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public void run() {
            List<OrderBookUpdateModel> orderBookModel = orderBookService.getOrderBookModel(symbol);
            for (OrderBookUpdateModel orderBookUpdateModel : orderBookModel) {
                addEvent(orderBookUpdateModel);
            }

        }
    }

    @Async
    public void addEvent(OrderBookUpdateModel orderBookUpdateModel){
        symbolVsOrderQueue.get(orderBookUpdateModel.getSymbol()).offer(orderBookUpdateModel);
    }

    public void addTickerPrice(TickerPrice tickerPrice){
        symbolVsTicketPrices.computeIfAbsent(tickerPrice.getSymbol(), k-> new LinkedList<>());
        Deque<TickerPrice> queue = symbolVsTicketPrices.get(tickerPrice.getSymbol());

        while (queue.size() >= 10){ // 10 records for 10 minutes
            queue.poll();
        }
        queue.offer(tickerPrice);
    }


    public void updateBTCUSDTPrice(TickerStatistics btcusdtPrice){
        this.btcusdtPrice = btcusdtPrice;
    }
}
