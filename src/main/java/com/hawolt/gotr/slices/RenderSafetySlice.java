package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.minigame.impl.RegionUpdateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

public class RenderSafetySlice extends AbstractPluginSlice {
    @Getter(AccessLevel.PUBLIC)
    private boolean isInGame, isVolatileState, widgetAvailable, widgetVisible;

    @Getter(AccessLevel.NONE)
    private int currentRegionId;

    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onRegionUpdateEvent(RegionUpdateEvent event) {
        this.currentRegionId = event.getCurrentRegionId();
        this.isInGame = currentRegionId == StaticConstant.MINIGAME_REGION_ID;
        this.bus.post(new RenderSafetyEvent(isVolatileState, isInGame, widgetAvailable, widgetVisible));
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        this.isVolatileState = StaticConstant.VOLATILE_GAME_STATES.contains(event.getGameState());
        this.bus.post(new RenderSafetyEvent(isVolatileState, isInGame, widgetAvailable, widgetVisible));
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        Widget widget = client.getWidget(
                StaticConstant.MINIGAME_WIDGET_GROUP_ID,
                StaticConstant.MINIGAME_WIDGET_CHILD_GAME_ID
        );
        boolean widgetAvailable = null != widget;
        boolean widgetVisible = widgetAvailable && !widget.isHidden();
        if (widgetAvailable == this.widgetAvailable && widgetVisible == this.widgetVisible) return;
        this.widgetAvailable = widgetAvailable;
        this.widgetVisible = widgetVisible;
        this.bus.post(new RenderSafetyEvent(isVolatileState, isInGame, widgetAvailable, widgetVisible));
    }
}
