package com.hawolt.gotr.simulator;

import com.hawolt.gotr.data.RuneCraftInfo;
import lombok.Getter;

public class SimulatedInventory {
    private final SimulatedPouch simulatedPouch;

    private int
            availableEssenceInInventory,
            availableFreeInventorySlots;

    private boolean isFirstCraft = true;

    @Getter
    private double totalRunesCrafted;

    public SimulatedInventory(
            int availableEssenceInInventory,
            int availableFreeInventorySlots,
            SimulatedPouch simulatedPouch
    ) {
        this.availableEssenceInInventory = availableEssenceInInventory;
        this.availableFreeInventorySlots = availableFreeInventorySlots;
        this.simulatedPouch = simulatedPouch;
    }

    public boolean craft(SimulatedEnvironment environment) {
        if (availableFreeInventorySlots == 0 && availableEssenceInInventory == 0) return false;
        if (availableEssenceInInventory == 0 && simulatedPouch.isEmpty()) return false;
        if (availableFreeInventorySlots > 0) {
            int essenceFromPouch = simulatedPouch.empty(availableFreeInventorySlots);
            this.availableFreeInventorySlots -= essenceFromPouch;
            this.availableEssenceInInventory += essenceFromPouch;
        }
        RuneCraftInfo runeCraftInfo = environment.getRuneCraftInfo();
        this.totalRunesCrafted += !runeCraftInfo.isCombinationRune() ?
                availableEssenceInInventory :
                environment.isBindingNecklaceChargeAvailable() ?
                        availableEssenceInInventory :
                        availableEssenceInInventory / 2D;
        if (environment.isBindingNecklaceChargeAvailable()) {
            environment.consumeBindingNecklaceCharge();
        }
        this.availableFreeInventorySlots += availableEssenceInInventory;
        if (isFirstCraft) {
            this.availableFreeInventorySlots -= 1;
            this.isFirstCraft = false;
        }
        this.availableEssenceInInventory = 0;
        return true;
    }

    public static SimulatedInventory create(
            int availableEssenceInInventory,
            int availableFreeInventorySlots,
            SimulatedPouch simulatedPouch
    ) {
        return new SimulatedInventory(
                availableEssenceInInventory,
                availableFreeInventorySlots,
                simulatedPouch

        );
    }
}
