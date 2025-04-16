package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.eventbus.Subscribe;

public class MuteSlice extends AbstractPluginSlice {
    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event) {
        if (event.getActor().getName() == null) return;
        if (!plugin.getConfig().isHideApprenticeHelpMessages()) return;
        if (!event.getActor().getName().matches("Apprentice (Tamara|Cordelia)")) return;
        event.getActor().setOverheadText("");
    }
}
