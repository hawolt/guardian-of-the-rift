package com.hawolt.gotr.events;

import lombok.AccessLevel;
import lombok.Getter;

public class FragmentAmountUpdateEvent {
    @Getter(AccessLevel.PUBLIC)
    private int previous, current;

    public FragmentAmountUpdateEvent(int previous, int current) {
        this.previous = previous;
        this.current = current;
    }

    @Override
    public String toString() {
        return "FragmentAmountUpdateEvent{" +
                "previous=" + previous +
                ", current=" + current +
                '}';
    }
}
