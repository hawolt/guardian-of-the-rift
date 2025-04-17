package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class ObeliskUpdateEvent extends AbstractMinigameEvent {
    private final int elementalRuneEnumIndex, catalyticRuneEnumIndex, remainingTicksUntilUpdate;

    public ObeliskUpdateEvent(
            int clientTick,
            int elementalRuneEnumIndex,
            int catalyticRuneEnumIndex,
            int remainingTicksUntilUpdate
    ) {
        super(clientTick);
        this.elementalRuneEnumIndex = elementalRuneEnumIndex;
        this.catalyticRuneEnumIndex = catalyticRuneEnumIndex;
        this.remainingTicksUntilUpdate = remainingTicksUntilUpdate;
    }

    @Override
    public String toString() {
        return "ObeliskUpdateEvent{" +
                "elementalRuneEnumIndex=" + elementalRuneEnumIndex +
                ", catalyticRuneEnumIndex=" + catalyticRuneEnumIndex +
                ", remainingTicksUntilUpdate=" + remainingTicksUntilUpdate +
                ", clientTick=" + clientTick +
                '}';
    }
}
