package com.hawolt.gotr.events.minigame.impl;

import com.hawolt.gotr.events.minigame.AbstractMinigameEvent;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PUBLIC)
public class GuardianDespawnEvent extends AbstractMinigameEvent {
    private final int currentAmountOfGuardians;

    public GuardianDespawnEvent(int clientTick, int currentAmountOfGuardians) {
        super(clientTick);
        this.currentAmountOfGuardians = currentAmountOfGuardians;
    }

    @Override
    public String toString() {
        return "GuardianDespawnEvent{" +
                "currentAmountOfGuardians=" + currentAmountOfGuardians +
                ", clientTick=" + clientTick +
                '}';
    }
}
