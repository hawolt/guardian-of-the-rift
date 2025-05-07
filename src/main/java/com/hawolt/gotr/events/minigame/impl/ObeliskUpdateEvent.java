package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class ObeliskUpdateEvent extends AbstractMinigameEvent {
    private final int elementalRuneEnumIndex, catalyticRuneEnumIndex;

    public ObeliskUpdateEvent(
            int clientTick,
            int elementalRuneEnumIndex,
            int catalyticRuneEnumIndex
    ) {
        super(clientTick);
        this.elementalRuneEnumIndex = elementalRuneEnumIndex;
        this.catalyticRuneEnumIndex = catalyticRuneEnumIndex;
    }

    @Override
    public String toString() {
        return "ObeliskUpdateEvent{" +
                "elementalRuneEnumIndex=" + elementalRuneEnumIndex +
                ", catalyticRuneEnumIndex=" + catalyticRuneEnumIndex +
                ", clientTick=" + clientTick +
                '}';
    }
}
