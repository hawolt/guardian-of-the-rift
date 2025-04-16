package com.hawolt.gotr.events.minigame;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public abstract class AbstractMinigameEvent {

    private final int clientTick;

    public AbstractMinigameEvent(int clientTick) {
        this.clientTick = clientTick;
    }
}
