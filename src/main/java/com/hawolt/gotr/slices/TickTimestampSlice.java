package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.events.TickTimestampEvent;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

public class TickTimestampSlice extends AbstractPluginSlice {
    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        this.bus.post(new TickTimestampEvent(client.getTickCount(), System.currentTimeMillis()));
    }
}
