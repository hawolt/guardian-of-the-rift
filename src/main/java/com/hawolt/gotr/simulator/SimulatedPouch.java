package com.hawolt.gotr.simulator;

import lombok.Getter;

public class SimulatedPouch {

    @Getter
    private int availableEssenceInPouch;

    public SimulatedPouch(int availableEssenceInPouch) {
        this.availableEssenceInPouch = availableEssenceInPouch;
    }

    public int empty(int requested) {
        if (isEmpty()) return 0;
        int availableForReturn;
        if (requested >= availableEssenceInPouch) {
            availableForReturn = availableEssenceInPouch;
        } else {
            availableForReturn = requested;
        }
        this.availableEssenceInPouch -= availableForReturn;
        return availableForReturn;
    }

    public boolean isEmpty() {
        return availableEssenceInPouch == 0;
    }

    public static SimulatedPouch create(int availableEssenceInPouch) {
        return new SimulatedPouch(availableEssenceInPouch);
    }
}
