package com.css.delivery;


import static org.mockito.ArgumentMatchers.*;

import java.util.concurrent.CompletableFuture;

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

import com.google.gson.Gson;

import lombok.SneakyThrows;

@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class, Gson.class, Dispatcher.class, Delivery.class, LogManager.class})
@PowerMockIgnore("javax.management.*")
public class AppTest {

    @SneakyThrows
    @Test
    public void testMain() {
        Logger logger = Mockito.spy(Logger.class);
        PowerMockito.mockStatic(LogManager.class);
        PowerMockito.when(LogManager.getLogger(App.class)).thenReturn(logger);

        PowerMockito.spy(System.class);
        PowerMockito.doThrow(new IllegalArgumentException()).when(System.class);
        System.exit(1);

        Assert.assertThrows(IllegalArgumentException.class, () -> App.main(new String[0]));
        Assert.assertThrows(IllegalArgumentException.class, () -> App.main(new String[]{"-s", "unknown"}));

        PowerMockito.spy(Thread.class);
        PowerMockito.doAnswer(invocation -> {
            long milliseconds = invocation.getArgument(0);
            System.out.println("Sleep for " + milliseconds + " milliseconds");
            return null;
        }).when(Thread.class);
        Thread.sleep(anyLong());

        Gson gson = PowerMockito.mock(Gson.class);
        PowerMockito.whenNew(Gson.class).withNoArguments().thenReturn(gson);
        Order[] orders = new Order[10];
        PowerMockito.when(gson.fromJson(anyString(), eq(Order[].class))).thenReturn(orders);
        Dispatcher dispatcher = PowerMockito.mock(Dispatcher.class);
        Delivery delivery = PowerMockito.mock(Delivery.class);
        long orderWaitTime = 1000;
        long courierWaitTime = 500;
        PowerMockito.when(delivery.getOrderWaitTime()).thenReturn(orderWaitTime);
        PowerMockito.when(delivery.getCourierWaitTime()).thenReturn(courierWaitTime);

        PowerMockito.when(dispatcher.start()).thenReturn(CompletableFuture.runAsync(() -> {
            App.getDeliveries().add(delivery);
        }, App.getExecutor()));
        PowerMockito.whenNew(Dispatcher.class).withAnyArguments().thenReturn(dispatcher);

        App.main(new String[]{"-s", "FIFO"});

        PowerMockito.verifyStatic(Thread.class, Mockito.times(orders.length / 2));
        Thread.sleep(1000);

        Mockito.verify(logger).info(startsWith("Average food wait time"), eq(orderWaitTime));
        Mockito.verify(logger).info(startsWith("Average courier wait time"), eq(courierWaitTime));
    }
}
