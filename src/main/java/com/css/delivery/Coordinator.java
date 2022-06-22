package com.css.delivery;

public interface Coordinator {

    void handleOrderReady(Order order);

    void handleCourierArrive(Courier courier);
}
