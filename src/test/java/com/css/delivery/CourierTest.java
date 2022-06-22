package com.css.delivery;

import static org.mockito.ArgumentMatchers.*;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lombok.SneakyThrows;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Courier.class, Delivery.class})
@PowerMockIgnore("javax.management.*")
public class CourierTest {
    @SneakyThrows
    @Test
    public void testGo() {
        PowerMockito.spy(Thread.class);
        PowerMockito.doNothing().when(Thread.class);
        Thread.sleep(anyLong());
        Order order = new Order();
        Courier courier = new Courier(order);
        courier.go();
        PowerMockito.verifyStatic(Thread.class);
        Thread.sleep(courier.getTripTime());
    }

    @SneakyThrows
    @Test
    public void testPickup() {
        Order order = new Order();
        Courier courier = new Courier(order);
        Delivery delivery = PowerMockito.mock(Delivery.class);
        PowerMockito.whenNew(Delivery.class).withArguments(eq(order), eq(courier), any(Instant.class)).thenReturn(delivery);
        courier.pickup(order);
        Assert.assertEquals(delivery, App.getDeliveries().peek());
        Mockito.verify(delivery).show();
    }
}
