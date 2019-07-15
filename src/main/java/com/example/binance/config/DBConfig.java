package com.example.binance.config;
/* Created by Ahmed Saifi on 14/07/19. */

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class DBConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DBConfig.class);

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.user}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    @Bean
    public InfluxDB influxDB() {
        InfluxDB influxDB = InfluxDBFactory.connect(dbUrl, dbUser, dbPassword);
        Pong response = influxDB.ping();
        if (response.getVersion().equalsIgnoreCase("unknown")) {
            LOG.error("Error pinging server.");
        }

        if(!influxDB.databaseExists("binance")) {
            influxDB.createDatabase("binance");
            influxDB.createRetentionPolicy(
                    "defaultPolicy", "binance", "30d", 1, true);
        }

        influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);

        influxDB.enableBatch(100, 20000, TimeUnit.MILLISECONDS);
        influxDB.setRetentionPolicy("defaultPolicy");
        influxDB.setDatabase("binance");
        return influxDB;
    }
}
