package com.css.delivery;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.SneakyThrows;

@Data
public class Order {
    private static final Logger logger = LogManager.getLogger(Order.class);

    private String id;
    private String name;
    // order preparation time in seconds
    private int prepTime;

    private volatile boolean ready;
    private volatile Instant readyTime;

    @SneakyThrows
    public void prepare() {
        Thread.sleep(1000L * this.getPrepTime());
        this.setReady(true);
        this.setReadyTime(Instant.now());
        logger.info("Order {} is ready", this.getName());
    }

}
