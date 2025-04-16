package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.StaticConstant;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;

@Getter
public class BindingNecklaceSlice extends AbstractPluginSlice {

    @Getter(AccessLevel.PUBLIC)
    private int bindingNecklaceCharges;

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() != -1) return;
        if (event.getVarpId() != StaticConstant.BINDING_NECKLACE_VARP_ID) return;
        this.bindingNecklaceCharges = event.getValue();
    }

    @Override
    protected void startUp() {
        this.bindingNecklaceCharges = client.getVarpValue(StaticConstant.BINDING_NECKLACE_VARP_ID);
    }

    @Override
    protected void shutDown() {

    }
}
