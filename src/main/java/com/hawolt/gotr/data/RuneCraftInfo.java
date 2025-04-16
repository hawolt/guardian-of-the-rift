package com.hawolt.gotr.data;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@Getter
public enum RuneCraftInfo {
    AIR(StaticConstant.MINIGAME_AIR_OBELISK_ID, 5, 1, ItemID.AIRRUNE, false, null),
    MIND(StaticConstant.MINIGAME_MIND_OBELISK_ID, 5.5, 2, ItemID.MINDRUNE, false, null),
    WATER(StaticConstant.MINIGAME_WATER_OBELISK_ID, 6, 5, ItemID.WATERRUNE, false, null),
    MIST_CRAFTED_ON_AIR(StaticConstant.MINIGAME_AIR_OBELISK_ID, 8, 6, ItemID.MISTRUNE, true, RuneCraftInfo.AIR),
    MIST_CRAFTED_ON_WATER(StaticConstant.MINIGAME_WATER_OBELISK_ID, 8.5, 6, ItemID.MISTRUNE, true, RuneCraftInfo.WATER),
    EARTH(StaticConstant.MINIGAME_EARTH_OBELISK_ID, 6.5, 9, ItemID.EARTHRUNE, false, null),
    DUST_CRAFTED_ON_AIR(StaticConstant.MINIGAME_AIR_OBELISK_ID, 8.3, 10, ItemID.DUSTRUNE, true, RuneCraftInfo.AIR),
    DUST_CRAFTED_ON_EARTH(StaticConstant.MINIGAME_EARTH_OBELISK_ID, 9, 10, ItemID.DUSTRUNE, true, RuneCraftInfo.EARTH),
    MUD_CRAFTED_ON_WATER(StaticConstant.MINIGAME_WATER_OBELISK_ID, 9.3, 13, ItemID.MUDRUNE, true, RuneCraftInfo.WATER),
    MUD_CRAFTED_ON_EARTH(StaticConstant.MINIGAME_EARTH_OBELISK_ID, 9.5, 13, ItemID.MUDRUNE, true, RuneCraftInfo.EARTH),
    FIRE(StaticConstant.MINIGAME_FIRE_OBELISK_ID, 7, 14, ItemID.FIRERUNE, false, null),
    SMOKE_CRAFTED_ON_AIR(StaticConstant.MINIGAME_AIR_OBELISK_ID, 8.5, 15, ItemID.SMOKERUNE, true, RuneCraftInfo.AIR),
    SMOKE_CRAFTED_ON_FIRE(StaticConstant.MINIGAME_FIRE_OBELISK_ID, 9.5, 15, ItemID.SMOKERUNE, true, RuneCraftInfo.FIRE),
    STEAM_CRAFTED_ON_WATER(StaticConstant.MINIGAME_WATER_OBELISK_ID, 9.3, 19, ItemID.STEAMRUNE, true, RuneCraftInfo.WATER),
    STEAM_CRAFTED_ON_FIRE(StaticConstant.MINIGAME_FIRE_OBELISK_ID, 10, 19, ItemID.STEAMRUNE, true, RuneCraftInfo.FIRE),
    BODY(StaticConstant.MINIGAME_BODY_OBELISK_ID, 7.5, 20, ItemID.BODYRUNE, false, null),
    LAVA_CRAFTED_ON_EARTH(StaticConstant.MINIGAME_EARTH_OBELISK_ID, 10, 23, ItemID.LAVARUNE, true, RuneCraftInfo.EARTH),
    LAVA_CRAFTED_ON_FIRE(StaticConstant.MINIGAME_FIRE_OBELISK_ID, 10.5, 23, ItemID.LAVARUNE, true, RuneCraftInfo.FIRE),
    COSMIC(StaticConstant.MINIGAME_COSMIC_OBELISK_ID, 8, 27, ItemID.COSMICRUNE, false, null),
    CHAOS(StaticConstant.MINIGAME_CHAOS_OBELISK_ID, 8.5, 35, ItemID.CHAOSRUNE, false, null),
    NATURE(StaticConstant.MINIGAME_NATURE_OBELISK_ID, 9, 44, ItemID.NATURERUNE, false, null),
    LAW(StaticConstant.MINIGAME_LAW_OBELISK_ID, 9.5, 54, ItemID.LAWRUNE, false, null),
    DEATH(StaticConstant.MINIGAME_DEATH_OBELISK_ID, 10, 65, ItemID.DEATHRUNE, false, null),
    BLOOD(StaticConstant.MINIGAME_BLOOD_OBELISK_ID, 10.5, 77, ItemID.BLOODRUNE, false, null);

    private final int guardianGameObjectId, levelRequired, spriteId;
    private final RuneCraftInfo baseRuneCraftInfo;
    private final boolean isCombinationRune;
    private final double baseExperience;

    RuneCraftInfo(
            int guardianGameObjectId,
            double baseExperience,
            int levelRequired,
            int spriteId,
            boolean isCombinationRune,
            RuneCraftInfo baseRuneCraftInfo
    ) {
        this.guardianGameObjectId = guardianGameObjectId;
        this.baseRuneCraftInfo = baseRuneCraftInfo;
        this.isCombinationRune = isCombinationRune;
        this.baseExperience = baseExperience;
        this.levelRequired = levelRequired;
        this.spriteId = spriteId;
    }

    @Override
    public String toString() {
        return name().split("_")[0];
    }

    public static RuneCraftInfo find(GuardianOfTheRiftOptimizerConfig config, Obelisk obelisk) {
        switch (obelisk) {
            case AIR:
                return config.airAltar().relation;
            case MIND:
                return RuneCraftInfo.MIND;
            case WATER:
                return config.waterAltar().relation;
            case EARTH:
                return config.earthAltar().relation;
            case FIRE:
                return config.fireAltar().relation;
            case BODY:
                return RuneCraftInfo.BODY;
            case COSMIC:
                return RuneCraftInfo.COSMIC;
            case CHAOS:
                return RuneCraftInfo.CHAOS;
            case NATURE:
                return RuneCraftInfo.NATURE;
            case LAW:
                return RuneCraftInfo.LAW;
            case DEATH:
                return RuneCraftInfo.DEATH;
            case BLOOD:
                return RuneCraftInfo.BLOOD;
            default:
                throw new IllegalArgumentException("Unknown Obelisk: " + obelisk);
        }
    }
}
