package com.hawolt.gotr.events;

import lombok.AccessLevel;
import lombok.Getter;

public class EssenceAmountUpdateEvent {
    @Getter(AccessLevel.PUBLIC)
    private int previous, current;

    public EssenceAmountUpdateEvent(int previous, int current) {
        this.previous = previous;
        this.current = current;
    }

    @Override
    public String toString() {
        return "EssenceAmountUpdateEvent{" +
                "previous=" + previous +
                ", current=" + current +
                '}';
    }
}
