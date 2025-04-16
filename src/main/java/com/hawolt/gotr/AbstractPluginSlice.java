package com.hawolt.gotr;

import net.runelite.api.Client;
import net.runelite.client.eventbus.EventBus;

import javax.inject.Inject;

public abstract class AbstractPluginSlice implements Slice {
    @Inject
    protected Client client;
    @Inject
    protected EventBus bus;
    @Inject
    protected GuardianOfTheRiftOptimizerPlugin plugin;

    @Override
    public void startup() {
        this.bus.register(this);
        this.startUp();
    }

    @Override
    public void shutdown() {
        this.bus.unregister(this);
        this.shutDown();
    }

    protected abstract void startUp();

    protected abstract void shutDown();
}
