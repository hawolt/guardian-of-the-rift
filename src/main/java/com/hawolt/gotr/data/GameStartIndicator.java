package com.hawolt.gotr.data;

import lombok.Getter;

@Getter
public enum GameStartIndicator {
    PHASE1(30),
    PHASE2(10),
    PHASE3(5),
    OFF(-1);

    private final int remaining;

    GameStartIndicator(int remaining) {
        this.remaining = remaining;
    }

    @Override
    public String toString() {
        return remaining == -1 ? "OFF" : remaining + " Seconds";
    }
}
