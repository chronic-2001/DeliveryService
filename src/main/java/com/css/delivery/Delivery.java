package com.css.delivery;

import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;

@Data
public final class Delivery {
    private static final Logger logger = LogManager.getLogger(Delivery.class);

    private final Order order;
    private final Courier courier;
    private final Instant pickupTime;

    private long orderWaitTime = -1;
    private long courierWaitTime = -1;

    public long getOrderWaitTime() {
        if (orderWaitTime == -1) {
            orderWaitTime = Duration.between(order.getReadyTime(), pickupTime).toMillis();
        }
        return orderWaitTime;
    }

    public long getCourierWaitTime() {
        if (courierWaitTime == -1) {
            courierWaitTime = Duration.between(courier.getArriveTime(), pickupTime).toMillis();
        }
        return courierWaitTime;
    }

    public void show() {
        logger.info("Order {} waited {} milliseconds to be picked up by courier {}",
                order.getName(), getOrderWaitTime(), courier.getId());
        logger.info("Courier {} waited {} milliseconds to pickup order {}",
                courier.getId(), getCourierWaitTime(), order.getName());
    }

}
