package com.hawolt.gotr;

public interface Slice {
    void startup();

    void shutdown();

    boolean isClientThreadRequiredOnStartup();

    boolean isClientThreadRequiredOnShutDown();
}