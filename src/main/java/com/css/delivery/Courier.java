package com.css.delivery;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.SneakyThrows;

@Data
public class Courier {
    private static final Logger logger = LogManager.getLogger(Courier.class);

    private static final AtomicInteger sequence = new AtomicInteger();

    // Time needed for a courier to arrive in milliseconds. Randomly chosen between 3-15 seconds.
    private final int tripTime = ThreadLocalRandom.current().nextInt(3000, 15001);
    private final int id = sequence.getAndIncrement();

    private final Order order;

    private volatile Instant arriveTime;

    @SneakyThrows
    public void go() {
        Thread.sleep(tripTime);
        this.setArriveTime(Instant.now());
        logger.info("Courier {} has arrived", this.getId());
    }

    public void pickup(Order order) {
        Delivery delivery = new Delivery(order, this, Instant.now());
        delivery.show();
        App.getDeliveries().offer(delivery);
    }

}
