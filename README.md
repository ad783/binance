# Binance

## Overview
Binance is a lightweight Java library for interacting with the Binance API, providing buy and sell strategy on the basis of historic data simulated as real time data

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

You need to install and set the following things before starting the application:

1. **JAVA 8**: [Install](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) It is requrired as application uses spring boot.
2. **InfluxDB**: [Install](https://docs.influxdata.com/influxdb/v1.7/introduction/installation/) Time series databse is required to store orderbook updates.
3. Run influxdb on port 8086 (default one)
4. Open CLI of influxdb by command `influxd -config /usr/local/etc/influxdb.conf`
5. Run the following command to create user db credentials:
    - ` CREATE USER influx_user WITH PASSWORD 'influx_password' WITH ALL PRIVILEGES`

### Installing

#### Local Installation

1. Clone the repository via ssh/http.
2. Run this command 'mvn spring-boot:run' from root of the project

GET `http://localhost:8080/api/v1/binance/getSymbols?fixedPairElement=BTC` to get All Symbols for BTC etc

GET `http://localhost:8080/api/v1/binance/getAllSymbols` to get All Symbols

GET `http://localhost:8080/api/v1/binance/getDepthCache?symbol=BNBBTC` to get the depth cache for a symbol

GET `http://localhost:8080/api/v1/order-book/getAllOrderBook` to get the all order book stored on local cache/db

GET `http://localhost:8080/api/v1/order-book/getOrderBook?symbol=BNBBTC` to get the all order book stored on local cache/db for a symbol

Hit `http://localhost:8080' to go to the homepage

![Binance Asks And Bids 1](./binance_png1.png)


![Binance Asks And Bids 2](./binance_png2.png)

You can now proceed to test the APIs using Postman or implement new features.
