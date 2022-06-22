package com.css.delivery;

import java.time.Instant;

import org.junit.Test;
import org.mockito.Mockito;

import lombok.SneakyThrows;

public class MatchedCoordinatorTest {
    @SneakyThrows
    @Test
    public void testHandle() {
        MatchedCoordinator coordinator = new MatchedCoordinator();
        Order order = new Order();
        Courier courier = new Courier(order);
        Thread orderThread = new Thread(() -> coordinator.handleOrderReady(order));
        Thread courierThread = new Thread(() -> coordinator.handleCourierArrive(courier));
        order.setReadyTime(Instant.now());
        courier.setArriveTime(Instant.now());
        courierThread.start();
        Thread.sleep(100);
        order.setReady(true);
        orderThread.start();
        orderThread.join();
        courierThread.join();
    }
}
