package com.css.delivery;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;

import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Delivery.class, LogManager.class})
@PowerMockIgnore("javax.management.*")
public class DeliveryTest {
    @Test
    public void testGetWaitTime() {
        Order order = new Order();
        order.setReadyTime(Instant.now());
        Courier courier = new Courier(order);
        courier.setArriveTime(Instant.now());
        Delivery delivery = new Delivery(order, courier, Instant.now());
        Assert.assertEquals(Duration.between(order.getReadyTime(), delivery.getPickupTime()).toMillis(),
                delivery.getOrderWaitTime());
        Assert.assertEquals(Duration.between(courier.getArriveTime(), delivery.getPickupTime()).toMillis(),
                delivery.getCourierWaitTime());
    }

    @Test
    public void testShow() {
        Order order = Mockito.mock(Order.class);
        Mockito.when(order.getName()).thenReturn("order");
        Courier courier = Mockito.mock(Courier.class);
        Mockito.when(courier.getId()).thenReturn(1);
        Delivery delivery = Mockito.spy(new Delivery(order, courier, Instant.now()));
        Mockito.doReturn(100L).when(delivery).getOrderWaitTime();
        Mockito.doReturn(100L).when(delivery).getCourierWaitTime();
        delivery.show();

        Mockito.verify(order, Mockito.times(2)).getName();
        Mockito.verify(courier, Mockito.times(2)).getId();
        Mockito.verify(delivery).getOrderWaitTime();
        Mockito.verify(delivery).getCourierWaitTime();
    }
}
