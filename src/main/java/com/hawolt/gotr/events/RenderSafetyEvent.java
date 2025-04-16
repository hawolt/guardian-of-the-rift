package com.hawolt.gotr.events;

import lombok.AccessLevel;
import lombok.Getter;

public class RenderSafetyEvent {
    @Getter(AccessLevel.PUBLIC)
    private final boolean isVolatileState, isInGame, isMinigameWidgetVisible;

    public RenderSafetyEvent(boolean isVolatileState, boolean isInGame, boolean isMinigameWidgetVisible) {
        this.isMinigameWidgetVisible = isMinigameWidgetVisible;
        this.isVolatileState = isVolatileState;
        this.isInGame = isInGame;
    }
}
