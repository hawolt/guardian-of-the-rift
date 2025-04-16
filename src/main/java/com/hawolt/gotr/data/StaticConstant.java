package com.hawolt.gotr.data;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.GameState;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class StaticConstant {

    // GAME MISC
    public static final long GAME_TICK_DURATION = 600L;
    public static final int INVENTORY_CONTAINER_ID = 93;
    public static final int BINDING_NECKLACE_VARP_ID = 487;
    public static final int BINDING_NECKLACE_ITEM_ID = 5521;
    public static final int MINIGAME_PORTAL_SPRITE_ID = 4368;

    // MINIGAME INTERNALS
    public static final int MINIGAME_REGION_ID = 14484;
    public static final int MINIGAME_HUD_UPDATE_SCRIPT_ID = 5980;

    // MINIGAME WIDGET INDICES
    public static final int MINIGAME_WIDGET_GROUP_ID = 746;
    public static final int MINIGAME_WIDGET_CHILD_GAME_ID = 1;
    public static final int MINIGAME_WIDGET_POWER_TEXT_WIDGET_ID = 18;
    public static final int MINIGAME_WIDGET_ELEMENTAL_TEXT_WIDGET_ID = 21;
    public static final int MINIGAME_WIDGET_CATALYTIC_TEXT_WIDGET_ID = 24;

    // MINIGAME WIDGETS IDS
    public static final int MINIGAME_WIDGET_PARENT_ID = 48889858;
    public static final int MINIGAME_WIDGET_PORTAL_ID = 48889882;
    public static final int MINIGAME_WIDGET_GUARDIAN_ID = 48889881;

    // MINIGAME OBJECTS
    public static final int MINIGAME_GREAT_GUARDIAN_NPC_ID = 11403;
    public static final int MINIGAME_PORTAL_OBJECT_ID = 43729;
    public static final int MINIGAME_UNCHARGED_CELL_TABLE_OBJECT_ID = 43732;
    public static final int MINIGAME_DEPOSIT_POOL_ID = 43696;

    // MINIGAME ITEM GROUP: GUARDIAN STONES
    public static final int MINIGAME_GUARDIAN_STONE_ID = 26879;
    public static final int MINIGAME_ELEMENTAL_GUARDIAN_STONE_ID = 26881;
    public static final int MINIGAME_CATALYTIC_GUARDIAN_STONE_ID = 26880;
    public static final int MINIGAME_POLYELEMENTAL_GUARDIAN_STONE_ID = 26941;

    // MINIGAME ITEMS OTHER
    public static final int MINIGAME_UNCHARGED_CELL_ID = 26882;

    // MINIGAME OBELISK OBJECTS
    public static final int MINIGAME_AIR_OBELISK_ID = 43701;
    public static final int MINIGAME_MIND_OBELISK_ID = 43705;
    public static final int MINIGAME_WATER_OBELISK_ID = 43702;
    public static final int MINIGAME_EARTH_OBELISK_ID = 43703;
    public static final int MINIGAME_FIRE_OBELISK_ID = 43704;
    public static final int MINIGAME_BODY_OBELISK_ID = 43709;
    public static final int MINIGAME_COSMIC_OBELISK_ID = 43710;
    public static final int MINIGAME_CHAOS_OBELISK_ID = 43706;
    public static final int MINIGAME_NATURE_OBELISK_ID = 43711;
    public static final int MINIGAME_LAW_OBELISK_ID = 43712;
    public static final int MINIGAME_DEATH_OBELISK_ID = 43707;
    public static final int MINIGAME_BLOOD_OBELISK_ID = 43708;

    // UTILITY DATA SETS
    public static final Set<Integer> MINIGAME_GUARDIAN_STONE_IDS = ImmutableSet.of(
            MINIGAME_ELEMENTAL_GUARDIAN_STONE_ID,
            MINIGAME_CATALYTIC_GUARDIAN_STONE_ID,
            MINIGAME_POLYELEMENTAL_GUARDIAN_STONE_ID
    );

    public static final Set<Integer> MINIGAME_CHARGED_CELL_IDS = Arrays.stream(ChargeableCellType.VALUES)
            .mapToInt(ChargeableCellType::getItemId)
            .boxed()
            .collect(Collectors.toSet());

    public static final Set<Integer> MINIGAME_TALISMAN_IDS = Arrays.stream(Obelisk.VALUES)
            .mapToInt(Obelisk::getTalismanItemId)
            .boxed()
            .collect(Collectors.toSet());

    public static final Set<Integer> MINIGAME_CELL_TILE_IDS = Arrays.stream(CellTile.VALUES)
            .mapToInt(CellTile::getGroundObjectId)
            .boxed()
            .collect(Collectors.toSet());

    public static final Set<Integer> MINIGAME_IDS_OBELISK_ID = Arrays.stream(Obelisk.VALUES)
            .mapToInt(Obelisk::getGameObjectId)
            .boxed()
            .collect(Collectors.toSet());

    public static final Set<GameState> VOLATILE_GAME_STATES = ImmutableSet.of(
            GameState.CONNECTION_LOST,
            GameState.HOPPING,
            GameState.LOADING
    );
}
