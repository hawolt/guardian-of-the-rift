package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class MinigameStateEvent extends AbstractMinigameEvent {
    private final MinigameState currentMinigameState, previousMinigameState;
    private final int internalGuardianTicksRemaining;

    public MinigameStateEvent(int clientTick, MinigameState currentMinigameState, MinigameState previousMinigameState, int internalGuardianTicksRemaining) {
        super(clientTick);
        this.internalGuardianTicksRemaining = internalGuardianTicksRemaining;
        this.currentMinigameState = currentMinigameState;
        this.previousMinigameState = previousMinigameState;
    }

    @Override
    public String toString() {
        return "MinigameStateEvent{" +
                "currentMinigameState=" + currentMinigameState +
                ", previousMinigameState=" + previousMinigameState +
                ", internalGuardianTicksRemaining=" + internalGuardianTicksRemaining +
                ", clientTick=" + clientTick +
                '}';
    }
}
