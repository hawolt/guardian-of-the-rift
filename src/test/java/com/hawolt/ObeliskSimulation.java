package com.hawolt;

import com.hawolt.gotr.data.Obelisk;
import com.hawolt.gotr.data.RuneCraftInfo;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.simulator.Simulator;

public class ObeliskSimulation {
    public static void main(String[] args) {
        int availableEmptyInventorySlots = 0;
        int availableEssenceInPouches = 27;
        int availableEssenceInInventory = 23;
        int bindingNecklaceCharges = 7;
        boolean isCellAvailable = true;
        boolean isBindingNecklaceEquipped = true;

        double nature = calculateEfficiency(
                Obelisk.NATURE,
                4,
                availableEmptyInventorySlots,
                availableEssenceInPouches,
                availableEssenceInInventory,
                bindingNecklaceCharges,
                RuneCraftInfo.NATURE,
                isCellAvailable,
                isBindingNecklaceEquipped
        );
        System.out.println("nature: " + nature);

        double fire = calculateEfficiency(
                Obelisk.FIRE,
                0,
                availableEmptyInventorySlots,
                availableEssenceInPouches,
                availableEssenceInInventory,
                bindingNecklaceCharges,
                RuneCraftInfo.STEAM_CRAFTED_ON_FIRE,
                isCellAvailable,
                isBindingNecklaceEquipped
        );
        System.out.println("fire: " + fire);
    }

    private static double calculateEfficiency(
            Obelisk obelisk,
            int normalizedTileDistance,
            int availableEmptyInventorySlots,
            int availableEssenceInPouches,
            int availableEssenceInInventory,
            int bindingNecklaceCharges,
            RuneCraftInfo runeCraftInfo,
            boolean isCellAvailable,
            boolean isBindingNecklaceEquipped
    ) {
        Simulator simulator = Simulator.createInstance(
                availableEssenceInInventory,
                availableEmptyInventorySlots,
                availableEssenceInPouches,
                bindingNecklaceCharges,
                runeCraftInfo
        );

        int totalEssence = availableEssenceInInventory + availableEssenceInPouches;
        double totalRuneYield = simulator.simulateTotalCraftedRunes();

        System.out.println(runeCraftInfo + ": " + totalRuneYield);

        int inside = normalizeTileCount(obelisk.getTileDistance()) << 1;
        int cellExperienceReward = isCellAvailable ?
                obelisk.getCellType().getExperienceReward() :
                0;

        System.out.println(runeCraftInfo + ": " + cellExperienceReward);

        long timeToWalk = (normalizedTileDistance + inside) * StaticConstant.GAME_TICK_DURATION;
        System.out.println(runeCraftInfo + ": " + timeToWalk);

        double baseExperienceYield = (cellExperienceReward + (runeCraftInfo.getBaseExperience() * totalRuneYield));
        System.out.println(runeCraftInfo + ": " + baseExperienceYield);

        double downGradeExperience = runeCraftInfo.isCombinationRune() ?
                cellExperienceReward + (runeCraftInfo.getBaseRuneCraftInfo().getBaseExperience() * totalEssence) :
                baseExperienceYield;

        boolean isDowngradeBetter;

        if (!runeCraftInfo.isCombinationRune()) {
            isDowngradeBetter = false;
        } else {
            if (isBindingNecklaceEquipped) {
                isDowngradeBetter = downGradeExperience > baseExperienceYield;
            } else {
                isDowngradeBetter = true;
            }
        }

        return ((isDowngradeBetter ? downGradeExperience : baseExperienceYield) / (double) timeToWalk) * 100D;
    }

    private static int normalizeTileCount(int amount) {
        return (amount % 2 == 0) ? amount : amount + 1;
    }

}
