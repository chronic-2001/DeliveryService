package com.css.delivery;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FIFOCoordinator implements Coordinator {
    private final Queue<Order> orders = new ConcurrentLinkedQueue<>();
    private final Queue<Courier> couriers = new ConcurrentLinkedQueue<>();

    @Override
    public void handleOrderReady(Order order) {
        Courier courier = couriers.poll();
        if (courier != null) {
            courier.pickup(order);
        } else {
            orders.offer(order);
        }
    }

    @Override
    public void handleCourierArrive(Courier courier) {
        Order order = orders.poll();
        if (order != null) {
            courier.pickup(order);
        } else {
            couriers.offer(courier);
        }
    }
}
