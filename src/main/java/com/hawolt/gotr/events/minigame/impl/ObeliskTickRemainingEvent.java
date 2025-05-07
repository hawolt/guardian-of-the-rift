package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class ObeliskTickRemainingEvent extends AbstractMinigameEvent {
    private final int remainingTicksUntilUpdate;

    public ObeliskTickRemainingEvent(
            int clientTick,
            int remainingTicksUntilUpdate
    ) {
        super(clientTick);
        this.remainingTicksUntilUpdate = remainingTicksUntilUpdate;
    }

    @Override
    public String toString() {
        return "ObeliskTickRemainingEvent{" +
                "remainingTicksUntilUpdate=" + remainingTicksUntilUpdate +
                ", clientTick=" + clientTick +
                '}';
    }
}
