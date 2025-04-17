package com.hawolt.gotr.events.minigame;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public abstract class AbstractMinigameEvent {

    protected final int clientTick;

    public AbstractMinigameEvent(int clientTick) {
        this.clientTick = clientTick;
    }

    @Override
    public String toString() {
        return "AbstractMinigameEvent{" +
                "clientTick=" + clientTick +
                '}';
    }
}
