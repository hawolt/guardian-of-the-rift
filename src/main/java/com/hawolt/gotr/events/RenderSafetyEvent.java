package com.hawolt.gotr.events;

import lombok.AccessLevel;
import lombok.Getter;

public class RenderSafetyEvent {
    @Getter(AccessLevel.PUBLIC)
    private final boolean isVolatileState, isInGame, isWidgetAvailable, isWidgetVisible;

    public RenderSafetyEvent(
            boolean isVolatileState,
            boolean isInGame,
            boolean isWidgetAvailable,
            boolean isWidgetVisible
    ) {
        this.isWidgetAvailable = isWidgetAvailable;
        this.isVolatileState = isVolatileState;
        this.isWidgetVisible = isWidgetVisible;
        this.isInGame = isInGame;
    }

    @Override
    public String toString() {
        return "RenderSafetyEvent{" +
                "isVolatileState=" + isVolatileState +
                ", isInGame=" + isInGame +
                ", isWidgetAvailable=" + isWidgetAvailable +
                ", isWidgetVisible=" + isWidgetVisible +
                '}';
    }
}
