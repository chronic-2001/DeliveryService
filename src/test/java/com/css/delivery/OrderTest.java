package com.css.delivery;

import static org.mockito.ArgumentMatchers.anyLong;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import lombok.SneakyThrows;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Order.class})
@PowerMockIgnore("javax.management.*")
public class OrderTest {
    @SneakyThrows
    @Test
    public void testPrepare() {
        PowerMockito.spy(Thread.class);
        PowerMockito.doNothing().when(Thread.class);
        Thread.sleep(anyLong());
        Order order = new Order();
        order.setId("id");
        order.setName("name");
        order.setPrepTime(10);
        order.prepare();
        PowerMockito.verifyStatic(Thread.class);
        Thread.sleep(1000L * order.getPrepTime());
    }
}
