package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class RegionUpdateEvent extends AbstractMinigameEvent {
    private final int currentRegionId, previousRegionId;

    public RegionUpdateEvent(int clientTick, int currentRegionId, int previousRegionId) {
        super(clientTick);
        this.previousRegionId = previousRegionId;
        this.currentRegionId = currentRegionId;
    }

    @Override
    public String toString() {
        return "RegionUpdateEvent{" +
                "currentRegionId=" + currentRegionId +
                ", previousRegionId=" + previousRegionId +
                ", clientTick=" + clientTick +
                '}';
    }
}
