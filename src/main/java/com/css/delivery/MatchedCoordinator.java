package com.css.delivery;

import lombok.SneakyThrows;

public class MatchedCoordinator implements Coordinator {
    @Override
    public void handleOrderReady(Order order) {
        synchronized (order) {
            order.notify();
        }
    }

    @SneakyThrows
    @Override
    public void handleCourierArrive(Courier courier) {
        Order order = courier.getOrder();
        while (!order.isReady()) {
            synchronized (order) {
                order.wait();
            }
        }
        courier.pickup(order);
    }
}
