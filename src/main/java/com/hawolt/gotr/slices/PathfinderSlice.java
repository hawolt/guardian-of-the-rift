package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class PathfinderSlice extends AbstractPluginSlice {
    @Inject
    private Client client;

    @Inject
    private ScheduledExecutorService executor;

    @Getter(AccessLevel.PUBLIC)
    private final List<WorldArea> blockedWorldAreaByNPC = new ArrayList<>();

    @Getter(AccessLevel.PUBLIC)
    private Map<Integer, Integer> objectBlocking, npcBlocking;

    @Override
    protected void startUp() {
        this.executor.execute(() -> {
            this.objectBlocking = load("/loc_blocking.txt");
            this.npcBlocking = load("/npc_blocking.txt");
        });
    }

    @Override
    protected void shutDown() {
        this.objectBlocking = null;
        this.npcBlocking = null;
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (objectBlocking == null || npcBlocking == null) return;
        this.updateBlockedByNPC();
    }

    private void updateBlockedByNPC() {
        List<NPC> npcs = client.getLocalPlayer().getWorldView().npcs()
                .stream()
                .collect(Collectors.toCollection(ArrayList::new));
        this.blockedWorldAreaByNPC.clear();
        for (NPC npc : npcs) {
            NPCComposition npcComposition = npc.getTransformedComposition();
            if (npcComposition == null) {
                continue;
            }
            if (getBlockingNPC(npcComposition.getId())) {
                this.blockedWorldAreaByNPC.add(npc.getWorldArea());
            }
        }
    }

    public int getObjectBlocking(final int objectId, final int rotation) {
        if (objectBlocking == null) return 0;
        int blockingValue = objectBlocking.getOrDefault(objectId, 0);
        return rotation == 0 ?
                blockingValue :
                (((blockingValue << rotation) & 0xF) + (blockingValue >> (4 - rotation)));
    }

    public boolean getBlockingNPC(final int npcCompId) {
        if (npcBlocking == null) return false;
        return npcBlocking.getOrDefault(npcCompId, 0) == 1;
    }

    private static Map<Integer, Integer> load(String resource) {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        try (InputStream inputStream = PathfinderSlice.class.getResourceAsStream(resource)) {
            if (inputStream == null) return map;
            String[] content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).split("\n");
            for (String line : content) {
                String[] split = line.trim().split("=");
                int id = Integer.parseInt(split[0]);
                int blocking = Integer.parseInt(split[1].split(" ")[0]);
                map.put(id, blocking);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return map;
    }
}
