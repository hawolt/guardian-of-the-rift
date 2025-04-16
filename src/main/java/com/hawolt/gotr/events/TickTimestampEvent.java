package com.hawolt.gotr.events;

import lombok.Getter;

@Getter
public class TickTimestampEvent {
    private final long timestamp;
    private final int tick;

    public TickTimestampEvent(int tick, long timestamp) {
        this.timestamp = timestamp;
        this.tick = tick;
    }
}
