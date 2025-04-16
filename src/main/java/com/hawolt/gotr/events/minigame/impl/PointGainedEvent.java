package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.data.TypeAssociation;
import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class PointGainedEvent extends AbstractMinigameEvent {
    private final TypeAssociation runeType;
    private final int gained, total;

    public PointGainedEvent(int clientTick, TypeAssociation runeType, int gained, int total) {
        super(clientTick);
        this.runeType = runeType;
        this.gained = gained;
        this.total = total;
    }
}
