package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class MilestoneEvent extends AbstractMinigameEvent {
    public MilestoneEvent(int clientTick) {
        super(clientTick);
    }
}
