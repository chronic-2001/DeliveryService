package com.css.delivery;

import org.junit.Test;
import org.mockito.Mockito;

public class FIFOCoordinatorTest {
    @Test
    public void testHandle() {
        FIFOCoordinator coordinator = new FIFOCoordinator();
        Order order = Mockito.mock(Order.class);
        Courier courier = Mockito.mock(Courier.class);
        coordinator.handleOrderReady(order);
        Mockito.verify(courier, Mockito.never()).pickup(order);
        coordinator.handleCourierArrive(courier);
        Mockito.verify(courier).pickup(order);
        coordinator.handleCourierArrive(courier);
        Mockito.verify(courier, Mockito.times(1)).pickup(order);
        coordinator.handleOrderReady(order);
        Mockito.verify(courier, Mockito.times(2)).pickup(order);
    }
}
