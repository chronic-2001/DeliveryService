package com.css.delivery;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;

@Data
public class Dispatcher {
    private static final Logger logger = LogManager.getLogger(Dispatcher.class);
    private final Order order;
    private final Courier courier;

    private final Coordinator coordinator;

    public Dispatcher(Order order, Coordinator coordinator) {
        this.order = order;
        this.courier = new Courier(order);
        this.coordinator = coordinator;
    }

    public CompletableFuture<Void> start() {
        return CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
            logger.info("Order {} received", order.getName());
            order.prepare();
            coordinator.handleOrderReady(order);
        }, App.getExecutor()), CompletableFuture.runAsync(() -> {
            logger.info("Courier {} dispatched", courier.getId());
            courier.go();
            coordinator.handleCourierArrive(courier);
        }, App.getExecutor()));
    }

}
