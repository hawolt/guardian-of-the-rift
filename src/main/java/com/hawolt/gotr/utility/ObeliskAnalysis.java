package com.hawolt.gotr.utility;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.*;
import com.hawolt.gotr.pathfinding.PathCreator;
import com.hawolt.gotr.pathfinding.Pathfinder;
import com.hawolt.gotr.simulator.Simulator;
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
    private int normalizedTileDistance;
    @Getter(AccessLevel.PUBLIC)
    private boolean isTalismanAvailable, isDowngradeBetter;

    @Getter(AccessLevel.NONE)
    private final GuardianOfTheRiftOptimizerPlugin plugin;
    @Getter(AccessLevel.NONE)
    private final OptimizationMode optimizationMode;
    @Getter(AccessLevel.NONE)
    private final PointStatus pointStatus;
    @Getter(AccessLevel.PUBLIC)
    private List<WorldPoint> pathToGuardian;

    public ObeliskAnalysis(
            GuardianOfTheRiftOptimizerPlugin plugin,
            Obelisk obelisk,
            PointStatus pointStatus,
            GameObject gameObject
    ) {
        this.plugin = plugin;
        this.obelisk = obelisk;
        this.gameObject = gameObject;
        this.pointStatus = pointStatus;
        this.optimizationMode = plugin.getConfig().optimizationMode();
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

        switch (optimizationMode) {
            case EXPERIENCE:
                return calculateExperienceWeight();
            case EVEN_REWARD_POINTS:
                return calculatePointWeight();
        }
        throw new RuntimeException("Unknown optimization mode " + optimizationMode);
    }

    private double calculatePointWeight() {
        double elementalRewardPoints = pointStatus.getElementalRewardPoints() + (pointStatus.getElementalPoints() / 100D);
        double catalyticRewardPoints = pointStatus.getCatalyticRewardPoints() + (pointStatus.getCatalyticPoints() / 100D);
        int outside = normalizeTileCount(pathToGuardian.size());
        int inside = normalizeTileCount(obelisk.getTileDistance()) << 1;
        this.normalizedTileDistance = outside;
        long timeToWalk = (outside + inside) * StaticConstant.GAME_TICK_DURATION;
        if (elementalRewardPoints > catalyticRewardPoints) {
            return obelisk.getTypeAssociation() == TypeAssociation.CATALYTIC ? (1D / timeToWalk) * 100000 : 0;
        } else if (catalyticRewardPoints > elementalRewardPoints) {
            return obelisk.getTypeAssociation() == TypeAssociation.ELEMENTAL ? (1D / timeToWalk) * 100000 : 0;
        } else {
            return calculateExperienceWeight();
        }
    }

    private double calculateExperienceWeight() {
        int availableEmptyInventorySlots = plugin.getInventoryEssenceSlice().getEmptyInventorySlots();
        int availableEssenceInPouches = plugin.getPouchEssenceSlice().getAvailableEssenceInPouches();
        int availableEssenceInInventory = plugin.getInventoryEssenceSlice().getEssenceInInventory();
        int bindingNecklaceCharges = plugin.getBindingNecklaceSlice().getBindingNecklaceCharges();

        Simulator simulator = Simulator.createInstance(
                availableEssenceInInventory,
                availableEmptyInventorySlots,
                availableEssenceInPouches,
                bindingNecklaceCharges,
                runeCraftInfo
        );

        int totalEssence = availableEssenceInInventory + availableEssenceInPouches;
        double totalRuneYield = simulator.simulateTotalCraftedRunes();

        int outside = normalizeTileCount(pathToGuardian.size());
        int inside = normalizeTileCount(obelisk.getTileDistance()) << 1;
        int cellExperienceReward = plugin.getInventoryEssenceSlice().isUnchargedCellAvailable() ?
                obelisk.getCellType().getExperienceReward() :
                0;

        this.normalizedTileDistance = outside;

        long timeToWalk = (outside + inside) * StaticConstant.GAME_TICK_DURATION;

        double baseExperienceYield = (cellExperienceReward + (runeCraftInfo.getBaseExperience() * totalRuneYield));

        double downGradeExperience = runeCraftInfo.isCombinationRune() ?
                cellExperienceReward + (runeCraftInfo.getBaseRuneCraftInfo().getBaseExperience() * totalEssence) :
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

        return ((isDowngradeBetter ? downGradeExperience : baseExperienceYield) / (double) timeToWalk) * 100D;
    }

    private static int normalizeTileCount(int amount) {
        return (amount % 2 == 0) ? amount : amount + 1;
    }

    @Override
    public String toString() {
        return "ObeliskAnalysis{" +
                "runeCraftInfo=" + runeCraftInfo +
                ", obelisk=" + obelisk +
                ", weightedEfficiency=" + weightedEfficiency +
                ", normalizedTileDistance=" + normalizedTileDistance +
                ", isTalismanAvailable=" + isTalismanAvailable +
                ", isDowngradeBetter=" + isDowngradeBetter +
                '}';
    }
}
