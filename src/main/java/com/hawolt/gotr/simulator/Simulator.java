package com.hawolt.gotr.simulator;

import com.hawolt.gotr.data.RuneCraftInfo;
import lombok.Getter;

@Getter
public class Simulator {

    private final SimulatedEnvironment simulatedEnvironment;
    private final SimulatedInventory simulatedInventory;
    private final SimulatedPouch simulatedPouch;

    public Simulator(
            int availableEssenceInInventory,
            int availableFreeInventorySlots,
            int availableEssenceInPouch,
            int bindingNecklaceCharges,
            RuneCraftInfo runeCraftInfo
    ) {
        this.simulatedPouch = SimulatedPouch.create(
                availableEssenceInPouch
        );
        this.simulatedInventory = SimulatedInventory.create(
                availableEssenceInInventory,
                availableFreeInventorySlots,
                simulatedPouch
        );
        this.simulatedEnvironment = SimulatedEnvironment.create(
                bindingNecklaceCharges,
                runeCraftInfo
        );
    }

    public double simulateTotalCraftedRunes() {
        boolean result;
        do {
            result = simulatedInventory.craft(simulatedEnvironment);
        } while (result);
        return simulatedInventory.getTotalRunesCrafted();
    }

    public static Simulator createInstance(
            int availableEssenceInInventory,
            int availableFreeInventorySlots,
            int availableEssenceInPouch,
            int bindingNecklaceCharges,
            RuneCraftInfo runeCraftInfo
    ) {
        return new Simulator(
                availableEssenceInInventory,
                availableFreeInventorySlots,
                availableEssenceInPouch,
                bindingNecklaceCharges,
                runeCraftInfo
        );
    }
}
