package com.hawolt.gotr.utility;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.Obelisk;
import com.hawolt.gotr.data.RuneCraftInfo;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.pathfinding.PathCreator;
import com.hawolt.gotr.pathfinding.Pathfinder;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ObeliskAnalysis {

    @Getter(AccessLevel.PUBLIC)
    private final RuneCraftInfo runeCraftInfo;
    @Getter(AccessLevel.PUBLIC)
    private final GameObject gameObject;
    @Getter(AccessLevel.PUBLIC)
    private final Obelisk obelisk;
    @Getter(AccessLevel.PUBLIC)
    private double weightedEfficiency;
    @Getter(AccessLevel.PUBLIC)
    private int remainingTicksUntilUpdate, currentClientTick, normalizedTileDistance;
    @Getter(AccessLevel.PUBLIC)
    private boolean isTalismanAvailable, isDowngradeBetter;

    @Getter(AccessLevel.NONE)
    private final GuardianOfTheRiftOptimizerPlugin plugin;
    @Getter(AccessLevel.PUBLIC)
    private List<WorldPoint> pathToGuardian;

    public ObeliskAnalysis(
            GuardianOfTheRiftOptimizerPlugin plugin,
            Obelisk obelisk,
            int currentClientTick,
            int remainingTicksUntilUpdate,
            GameObject gameObject
    ) {
        this.plugin = plugin;
        this.obelisk = obelisk;
        this.gameObject = gameObject;
        this.currentClientTick = currentClientTick;
        this.remainingTicksUntilUpdate = remainingTicksUntilUpdate;
        this.runeCraftInfo = RuneCraftInfo.find(plugin.getConfig(), obelisk);
        this.weightedEfficiency = calculateEfficiency();
        this.isTalismanAvailable = plugin.getInventoryEssenceSlice().getAvailableTalismanList().stream()
                .anyMatch(item -> item.getId() == obelisk.getTalismanItemId());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ObeliskAnalysis that = (ObeliskAnalysis) o;
        return obelisk == that.obelisk;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(obelisk);
    }

    /**
     * todo math is not perfectly correct currently,
     * binding necklace charges and essence remaining in inventory are not properly accounted for
     */
    private double calculateEfficiency() {
        Pathfinder pathfinder = new Pathfinder(plugin);
        Pair<List<WorldPoint>, Boolean> pathPair = PathCreator.pathTo(pathfinder, gameObject);

        this.pathToGuardian = PathCreator.make(
                plugin.getClient(),
                pathPair.getLeft(),
                plugin.getPathfinderSlice().getBlockedWorldAreaByNPC(),
                true,
                new LinkedList<>(),
                new LinkedList<>(),
                pathPair.getRight()
        );

        int availableEmptyInventorySlots = plugin.getInventoryEssenceSlice().getEmptyInventorySlots();
        int availableEssenceInPouches = plugin.getPouchEssenceSlice().getAvailableEssenceInPouches();
        int availableEssenceInInventory = plugin.getInventoryEssenceSlice().getEssenceInInventory();

        int totalRunesToCraft = !runeCraftInfo.isCombinationRune() ?
                availableEssenceInInventory + availableEssenceInPouches :
                calculateCraftableCombinationRuneAmount(
                        availableEssenceInPouches,
                        availableEssenceInInventory,
                        availableEmptyInventorySlots
                );

        int outside = normalizeTileCount(pathToGuardian.size());
        int inside = normalizeTileCount(obelisk.getTileDistance()) << 1;
        int cellExperienceReward = plugin.getInventoryEssenceSlice().isUnchargedCellAvailable() ?
                obelisk.getCellType().getExperienceReward() :
                0;

        this.normalizedTileDistance = outside;

        long timeToWalk = (outside + inside) * StaticConstant.GAME_TICK_DURATION;
        if (timeToWalk == 0) return -1;

        double baseExperienceYield = (cellExperienceReward + (runeCraftInfo.getBaseExperience() * totalRunesToCraft));

        double downGradeExperience = runeCraftInfo.isCombinationRune() ?
                cellExperienceReward + (runeCraftInfo.getBaseRuneCraftInfo().getBaseExperience() * totalRunesToCraft) :
                baseExperienceYield;

        if (!runeCraftInfo.isCombinationRune()) {
            this.isDowngradeBetter = false;
        } else {
            if (plugin.getEquipmentSlice().isBindingNecklaceEquipped()) {
                this.isDowngradeBetter = downGradeExperience > baseExperienceYield;
            } else {
                this.isDowngradeBetter = true;
            }
        }

        return (baseExperienceYield / (double) timeToWalk) * 100D;
    }

    private int calculateCraftableCombinationRuneAmount(
            int availableEssenceInPouches,
            int availableEssenceInInventory,
            int availableEmptyInventorySlots
    ) {
        int inventoriesToCraft = 1 + (int) Math.ceil(
                (availableEssenceInPouches / (double) (availableEssenceInPouches + availableEmptyInventorySlots))
        );

        int bindingNecklaceCharges = plugin.getBindingNecklaceSlice().getBindingNecklaceCharges();

        int inventoriesWithBindingNecklace = Math.min(bindingNecklaceCharges, inventoriesToCraft);

        int inventoriesWithoutBindingNecklace = inventoriesWithBindingNecklace != inventoriesToCraft ?
                inventoriesToCraft - bindingNecklaceCharges :
                0;

        int potentialEssenceInInventory = availableEssenceInInventory + availableEmptyInventorySlots;

        int totalRunesToCraftWithoutBindingNecklace = inventoriesWithoutBindingNecklace > 0 ?
                inventoriesWithoutBindingNecklace * potentialEssenceInInventory :
                0;

        int totalRunesCraftedWithBindingNecklace = inventoriesWithBindingNecklace > 0 ?
                (availableEssenceInInventory + availableEssenceInPouches) :
                inventoriesWithBindingNecklace * potentialEssenceInInventory;

        return totalRunesCraftedWithBindingNecklace + (totalRunesToCraftWithoutBindingNecklace >> 1);
    }

    private static int normalizeTileCount(int amount) {
        return (amount % 2 == 0) ? amount : amount + 1;
    }

}
