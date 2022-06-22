package com.css.delivery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lombok.SneakyThrows;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Dispatcher.class, Courier.class})
@PowerMockIgnore("javax.management.*")
public class DispatcherTest {

    @SneakyThrows
    @Test
    public void testStart() {
        Order order = Mockito.mock(Order.class);
        Courier courier = Mockito.mock(Courier.class);
        Coordinator coordinator = Mockito.mock(Coordinator.class);
        PowerMockito.whenNew(Courier.class).withArguments(order).thenReturn(courier);

        Dispatcher dispatcher = new Dispatcher(order, coordinator);
        dispatcher.start().join();

        Mockito.verify(order).prepare();
        Mockito.verify(coordinator).handleOrderReady(order);
        Mockito.verify(courier).go();
        Mockito.verify(coordinator).handleCourierArrive(courier);
    }
}
