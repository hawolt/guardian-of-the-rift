package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.data.TypeAssociation;
import com.hawolt.gotr.events.minigame.impl.*;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.eventbus.Subscribe;

public class MinigameSlice extends AbstractPluginSlice {

    @Getter(AccessLevel.NONE)
    private int
            elementalEnergy,
            catalyticEnergy,
            currentPower,
            maximumPower,
            portalLocation,
            elementalRuneEnumIndex,
            catalyticRuneEnumIndex,
            currentGuardians,
            maximumGuardians,
            guardianTicksRemaining,
            portalTicksRemaining,
            internalCurrentRegionId,
            internalElementalEnergy,
            internalCatalyticEnergy,
            internalCurrentPower,
            internalMaximumPower,
            internalPortalLocation,
            internalElementalRuneEnumIndex,
            internalCatalyticRuneEnumIndex,
            internalCurrentGuardians,
            internalMaximumGuardians,
            internalGuardianTicksRemaining,
            internalPortalTicksRemaining,
            currentRegionId,
            internalCurrentClientTick;

    @Getter(AccessLevel.NONE)
    private boolean
            isMilestoneDispatched,
            isEligibleForRewardDispatched,
            isRegionUpdateDispatched;

    @Getter(AccessLevel.NONE)
    private MinigameState minigameState;

    @Override
    public void startUp() {
        this.resetMinigameState();
    }

    @Override
    public void shutDown() {

    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        this.internalCurrentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        if (currentRegionId != internalCurrentRegionId) {
            this.handleRegionUpdateEvent();
        }
        this.currentPower = internalCurrentPower;
        this.maximumPower = internalMaximumPower;
        this.portalLocation = internalPortalLocation;
        this.elementalEnergy = internalElementalEnergy;
        this.catalyticEnergy = internalCatalyticEnergy;
        this.currentRegionId = internalCurrentRegionId;
        this.currentGuardians = internalCurrentGuardians;
        this.maximumGuardians = internalMaximumGuardians;
        this.portalTicksRemaining = internalPortalTicksRemaining;
        this.guardianTicksRemaining = internalGuardianTicksRemaining;
        this.elementalRuneEnumIndex = internalElementalRuneEnumIndex;
        this.catalyticRuneEnumIndex = internalCatalyticRuneEnumIndex;
    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOGGED_IN) return;
        this.resetMinigameState();
    }

    private void resetMinigameState() {
        this.internalCurrentPower = Integer.MAX_VALUE;
        this.minigameState = MinigameState.UNKNOWN;
        this.isEligibleForRewardDispatched = false;
        this.isRegionUpdateDispatched = false;
        this.isMilestoneDispatched = false;
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event) {
        if (event.getScriptId() != StaticConstant.MINIGAME_HUD_UPDATE_SCRIPT_ID) return;
        Object[] arguments = event.getScriptEvent().getArguments();
        this.internalElementalEnergy = Integer.parseInt(arguments[1].toString());
        this.internalCatalyticEnergy = Integer.parseInt(arguments[2].toString());
        this.internalCurrentPower = Integer.parseInt(arguments[3].toString());
        this.internalMaximumPower = Integer.parseInt(arguments[4].toString());
        this.internalPortalLocation = Integer.parseInt(arguments[5].toString());
        this.internalElementalRuneEnumIndex = Integer.parseInt(arguments[6].toString());
        this.internalCatalyticRuneEnumIndex = Integer.parseInt(arguments[7].toString());
        this.internalCurrentGuardians = Integer.parseInt(arguments[8].toString());
        this.internalMaximumGuardians = Integer.parseInt(arguments[9].toString());
        this.internalGuardianTicksRemaining = Integer.parseInt(arguments[10].toString());
        this.internalPortalTicksRemaining = Integer.parseInt(arguments[11].toString());
        this.internalCurrentClientTick = client.getTickCount();
        this.handleInternalGameState();
        this.handleInternalGameEvent();
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        boolean isPreviouslyUnknown = event.getPreviousMinigameState() == MinigameState.UNKNOWN;
        if (event.getCurrentMinigameState() == MinigameState.ACTIVE && isPreviouslyUnknown) {
            int guardianPower = (int) (((double) internalCurrentPower / (double) internalMaximumPower) * 100D);
            this.isMilestoneDispatched = guardianPower >= 60;
        }
        if (event.getCurrentMinigameState() != MinigameState.CLOSED) return;
        this.isMilestoneDispatched = false;
    }

    private void handleInternalGameState() {
        MinigameState minigameState = getMinigameState();
        if (this.minigameState == minigameState) return;

        this.bus.post(new MinigameStateEvent(internalCurrentClientTick, minigameState, this.minigameState));

        if (minigameState == MinigameState.CLOSING && this.minigameState != MinigameState.CLOSING) {
            this.bus.post(new GameStatusEvent(internalCurrentClientTick, currentPower == maximumPower));
        }

        this.minigameState = minigameState;
    }

    private MinigameState getMinigameState() {
        int technicallyStartingPower = internalMaximumPower / 10;
        boolean isGameInIdle = internalCatalyticRuneEnumIndex == 0 && internalElementalRuneEnumIndex == 0;
        MinigameState minigameState;
        if (internalMaximumPower == 0) {
            minigameState = MinigameState.INITIALIZING;
        } else if (internalCurrentPower == internalMaximumPower) {
            minigameState = MinigameState.COMPLETE;
        } else if (internalPortalLocation == 0 && internalCurrentPower == 0 && internalPortalTicksRemaining == -1) {
            minigameState = MinigameState.CLOSED;
        } else if (internalCurrentPower == 0 && internalPortalLocation == -1) {
            minigameState = MinigameState.CLOSING;
        } else if (internalCurrentPower == technicallyStartingPower && isGameInIdle) {
            minigameState = MinigameState.START;
        } else {
            minigameState = MinigameState.ACTIVE;
        }
        return minigameState;
    }

    private void handleInternalGameEvent() {
        this.handleMilestoneEvent();
        this.handlePointResetEvent();
        this.handlePortalSpawnEvent();
        this.handleObeliskUpdateEvent();
        this.handleGuardianSpawnEvent();
        this.handleGuardianDespawnEvent();
        this.handlePortalDespawnSpawnEvent();
        this.handleElementalPointGainEvent();
        this.handleCatalyticPointGainEvent();
    }

    private void handleGuardianDespawnEvent() {
        if (internalCurrentGuardians >= currentGuardians) return;
        this.bus.post(new GuardianDespawnEvent(internalCurrentClientTick, internalCurrentGuardians));
    }

    private void handleGuardianSpawnEvent() {
        if (internalCurrentGuardians <= currentGuardians) return;
        this.bus.post(new GuardianSpawnEvent(internalCurrentClientTick, internalCurrentGuardians));
    }

    private void handleObeliskUpdateEvent() {
        boolean isElementalEnumIndexUpdated = internalElementalRuneEnumIndex != elementalRuneEnumIndex;
        boolean isCatalyticEnumIndexUpdated = internalCatalyticRuneEnumIndex != catalyticRuneEnumIndex;
        if (!isElementalEnumIndexUpdated && !isCatalyticEnumIndexUpdated) return;
        this.bus.post(
                new ObeliskUpdateEvent(
                        client.getTickCount(),
                        internalElementalRuneEnumIndex,
                        internalCatalyticRuneEnumIndex,
                        internalGuardianTicksRemaining
                )
        );
    }

    private void handlePortalDespawnSpawnEvent() {
        if (internalPortalLocation > 0 || portalLocation <= 0) return;
        this.bus.post(new PortalDespawnEvent(internalCurrentClientTick));
    }

    private void handlePortalSpawnEvent() {
        if (internalPortalLocation <= 0 || portalLocation > 0) return;
        this.bus.post(new PortalSpawnEvent(internalCurrentClientTick, internalPortalTicksRemaining));
    }

    private void handlePointResetEvent() {
        if (internalElementalEnergy >= elementalEnergy) return;
        if (internalCatalyticEnergy >= catalyticEnergy) return;
        this.bus.post(new PointResetEvent(internalCurrentClientTick));
    }

    private void handleElementalPointGainEvent() {
        if (internalElementalEnergy <= elementalEnergy) return;
        this.bus.post(
                new PointGainedEvent(
                        internalCurrentClientTick,
                        TypeAssociation.ELEMENTAL,
                        internalElementalEnergy - elementalEnergy,
                        internalElementalEnergy
                )
        );
        this.handleRewardEligibilityEvent();
    }

    private void handleCatalyticPointGainEvent() {
        if (internalCatalyticEnergy <= catalyticEnergy) return;
        this.bus.post(
                new PointGainedEvent(
                        internalCurrentClientTick,
                        TypeAssociation.CATALYTIC,
                        internalCatalyticEnergy - catalyticEnergy,
                        internalCatalyticEnergy
                )
        );
        this.handleRewardEligibilityEvent();
    }

    private void handleRewardEligibilityEvent() {
        if ((internalElementalEnergy + internalCatalyticEnergy) < 300) return;
        this.isEligibleForRewardDispatched = true;
        this.bus.post(new RewardEligibleEvent(internalCurrentClientTick));
    }

    private void handleMilestoneEvent() {
        int guardianPower = (int) (((double) internalCurrentPower / (double) internalMaximumPower) * 100D);
        if (guardianPower < 60 || isMilestoneDispatched) return;
        this.isMilestoneDispatched = true;
        this.bus.post(new MilestoneEvent(internalCurrentClientTick));
    }

    private void handleRegionUpdateEvent() {
        if (internalCurrentRegionId == currentRegionId) return;
        this.bus.post(new RegionUpdateEvent(internalCurrentClientTick, internalCurrentRegionId, currentRegionId));
    }
}
