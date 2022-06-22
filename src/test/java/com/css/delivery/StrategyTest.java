package com.css.delivery;

import org.junit.Assert;
import org.junit.Test;

public class StrategyTest {
    @Test
    public void testCreateCoordinator() {
        Assert.assertTrue(Strategy.MATCHED.createCoordinator() instanceof MatchedCoordinator);
        Assert.assertTrue(Strategy.FIFO.createCoordinator() instanceof FIFOCoordinator);
    }
}
