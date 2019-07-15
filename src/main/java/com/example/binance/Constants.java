package com.example.binance;
/* Created by Ahmed Saifi on 14/07/19. */


public interface Constants {
    String BIDS  = "BIDS";
    String ASKS  = "ASKS";
    int ORDER_BOOK_DEFAULT_LIMIT = 10;
    int SYMBOL_BLOCKING_QUEUE_SIZE = 1000;
    int TEN_SECS = 10000;
    double BID_FACTOR = 0.97;
    double ASK_FACTOR = 1.03;
}
