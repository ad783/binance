package com.example.binance.model;
/* Created by Ahmed Saifi on 13/07/19. */


import java.math.BigDecimal;

public class PriceQuantity {
    private BigDecimal price;
    private BigDecimal quantity;

    public PriceQuantity() {
    }

    public PriceQuantity(BigDecimal price, BigDecimal quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
