package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class MinigameStateEvent extends AbstractMinigameEvent {
    private final MinigameState currentMinigameState, previousMinigameState;

    public MinigameStateEvent(int clientTick, MinigameState currentMinigameState, MinigameState previousMinigameState) {
        super(clientTick);
        this.currentMinigameState = currentMinigameState;
        this.previousMinigameState = previousMinigameState;
    }
}
