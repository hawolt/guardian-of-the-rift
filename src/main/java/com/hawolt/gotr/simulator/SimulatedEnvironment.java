package com.hawolt.gotr.simulator;

import com.hawolt.gotr.data.RuneCraftInfo;
import lombok.AccessLevel;
import lombok.Getter;

public class SimulatedEnvironment {

    @Getter(AccessLevel.PUBLIC)
    private final RuneCraftInfo runeCraftInfo;

    private int bindingNecklaceCharges;

    public SimulatedEnvironment(int bindingNecklaceCharges, RuneCraftInfo runeCraftInfo) {
        this.bindingNecklaceCharges = bindingNecklaceCharges;
        this.runeCraftInfo = runeCraftInfo;
    }

    public void consumeBindingNecklaceCharge() {
        this.bindingNecklaceCharges -= 1;
    }

    public boolean isBindingNecklaceChargeAvailable() {
        return bindingNecklaceCharges > 0;
    }

    public static SimulatedEnvironment create(int bindingNecklaceCharges, RuneCraftInfo runeCraftInfo) {
        return new SimulatedEnvironment(
                bindingNecklaceCharges,
                runeCraftInfo
        );
    }
}
