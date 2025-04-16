package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class GameStatusEvent extends AbstractMinigameEvent {

    private final boolean isGameWon;

    public GameStatusEvent(int clientTick, boolean isGameWon) {
        super(clientTick);
        this.isGameWon = isGameWon;
    }
}
