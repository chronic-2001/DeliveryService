package com.css.delivery;

public enum Strategy {
    MATCHED {
        @Override
        public Coordinator createCoordinator() {
            return new MatchedCoordinator();
        }
    },
    FIFO {
        @Override
        public Coordinator createCoordinator() {
            return new FIFOCoordinator();
        }
    };

    abstract public Coordinator createCoordinator();

}
