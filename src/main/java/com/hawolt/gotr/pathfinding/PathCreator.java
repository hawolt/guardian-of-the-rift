package com.hawolt.gotr.pathfinding;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathCreator {

    public static Pair<List<WorldPoint>, Boolean> pathTo(Pathfinder pathfinder, GameObject gameObject) {
        if (gameObject == null) return Pair.of(Collections.emptyList(), false);
        int objConfig = gameObject.getConfig();
        int sizeX = gameObject.sizeX();
        int sizeY = gameObject.sizeY();
        Point point = gameObject.getSceneMinLocation();
        return pathfinder.pathTo(point.getX(), point.getY(), sizeX, sizeY, objConfig, gameObject.getId());
    }

    private static List<WorldPoint> merge(Client client, List<WorldPoint> middlePathTiles, List<WorldPoint> pathTiles) {
        List<WorldPoint> list = new ArrayList<>();
        //   list.add(client.getLocalPlayer().getWorldLocation());
        list.addAll(middlePathTiles);
        list.addAll(pathTiles);
        return list;
    }

    public static List<WorldPoint> make(
            Client client,
            List<WorldPoint> checkpointWPs,
            List<WorldArea> npcBlockedWorldArea,
            boolean running,
            List<WorldPoint> middlePathTiles,
            List<WorldPoint> pathTiles,
            boolean pathFound
    ) {
        WorldArea currentWA = client.getLocalPlayer().getWorldArea();
        if (currentWA == null || checkpointWPs == null || checkpointWPs.isEmpty()) {
            return merge(client, middlePathTiles, pathTiles);
        }
        if ((currentWA.getPlane() != checkpointWPs.get(0).getPlane()) && pathFound) {
            return merge(client, middlePathTiles, pathTiles);
        }
        boolean runSkip = true;
        int cpTileIndex = 0;
        while (currentWA.toWorldPoint().getX() != checkpointWPs.get(checkpointWPs.size() - 1).getX()
                || currentWA.toWorldPoint().getY() != checkpointWPs.get(checkpointWPs.size() - 1).getY()) {
            WorldPoint cpTileWP = checkpointWPs.get(cpTileIndex);
            if (currentWA.toWorldPoint().equals(cpTileWP)) {
                cpTileIndex += 1;
                cpTileWP = checkpointWPs.get(cpTileIndex);
            }
            int dx = Integer.signum(cpTileWP.getX() - currentWA.getX());
            int dy = Integer.signum(cpTileWP.getY() - currentWA.getY());
            WorldArea finalCurrentWA = currentWA;
            boolean movementCheck = currentWA.canTravelInDirection(client.getTopLevelWorldView(), dx, dy, (worldPoint -> {
                WorldPoint worldPoint1 = new WorldPoint(finalCurrentWA.getX() + dx, finalCurrentWA.getY(), client.getLocalPlayer().getWorldView().getPlane());
                WorldPoint worldPoint2 = new WorldPoint(finalCurrentWA.getX(), finalCurrentWA.getY() + dy, client.getLocalPlayer().getWorldView().getPlane());
                WorldPoint worldPoint3 = new WorldPoint(finalCurrentWA.getX() + dx, finalCurrentWA.getY() + dy, client.getLocalPlayer().getWorldView().getPlane());
                for (WorldArea worldArea : npcBlockedWorldArea) {
                    if (worldArea.contains(worldPoint1) || worldArea.contains(worldPoint2) || worldArea.contains(worldPoint3)) {
                        return false;
                    }
                }
                return true;
            }));
            if (movementCheck) {
                currentWA = new WorldArea(currentWA.getX() + dx, currentWA.getY() + dy, 1, 1, client.getLocalPlayer().getWorldView().getPlane());
                if (currentWA.toWorldPoint().equals(checkpointWPs.get(checkpointWPs.size() - 1)) || !pathFound) {
                    pathTiles.add(currentWA.toWorldPoint());
                } else if (runSkip && running) {
                    middlePathTiles.add(currentWA.toWorldPoint());
                } else {
                    pathTiles.add(currentWA.toWorldPoint());
                }
                runSkip = !runSkip;
                continue;
            }
            movementCheck = currentWA.canTravelInDirection(client.getTopLevelWorldView(), dx, 0, (worldPoint -> {
                for (WorldArea worldArea : npcBlockedWorldArea) {
                    WorldPoint worldPoint1 = new WorldPoint(finalCurrentWA.getX() + dx, finalCurrentWA.getY(), client.getLocalPlayer().getWorldView().getPlane());
                    if (worldArea.contains(worldPoint1)) {
                        return false;
                    }
                }
                return true;
            }));
            if (dx != 0 && movementCheck) {
                currentWA = new WorldArea(currentWA.getX() + dx, currentWA.getY(), 1, 1, client.getLocalPlayer().getWorldView().getPlane());
                if (currentWA.toWorldPoint().equals(checkpointWPs.get(checkpointWPs.size() - 1)) || !pathFound) {
                    pathTiles.add(currentWA.toWorldPoint());
                } else if (runSkip && running) {
                    middlePathTiles.add(currentWA.toWorldPoint());
                } else {
                    pathTiles.add(currentWA.toWorldPoint());
                }
                runSkip = !runSkip;
                continue;
            }
            movementCheck = currentWA.canTravelInDirection(client.getTopLevelWorldView(), 0, dy, (worldPoint -> {
                for (WorldArea worldArea : npcBlockedWorldArea) {
                    WorldPoint worldPoint1 = new WorldPoint(finalCurrentWA.getX(), finalCurrentWA.getY() + dy, client.getLocalPlayer().getWorldView().getPlane());
                    if (worldArea.contains(worldPoint1)) {
                        return false;
                    }
                }
                return true;
            }));
            if (dy != 0 && movementCheck) {
                currentWA = new WorldArea(currentWA.getX(), currentWA.getY() + dy, 1, 1, client.getLocalPlayer().getWorldView().getPlane());
                if (currentWA.toWorldPoint().equals(checkpointWPs.get(checkpointWPs.size() - 1)) || !pathFound) {
                    pathTiles.add(currentWA.toWorldPoint());
                } else if (runSkip && running) {
                    middlePathTiles.add(currentWA.toWorldPoint());
                } else {
                    pathTiles.add(currentWA.toWorldPoint());
                }
                runSkip = !runSkip;
                continue;
            }
            return merge(client, middlePathTiles, pathTiles);
        }
        return merge(client, middlePathTiles, pathTiles);
    }
}
