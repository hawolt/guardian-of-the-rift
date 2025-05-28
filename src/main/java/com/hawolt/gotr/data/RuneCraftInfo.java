package com.hawolt.gotr.data;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@Getter
public enum RuneCraftInfo {
    AIR(
            StaticConstant.MINIGAME_AIR_OBELISK_ID,
            5,
            1,
            ItemID.AIRRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_AIR,
            false,
            null
    ),
    MIND(
            StaticConstant.MINIGAME_MIND_OBELISK_ID,
            5.5,
            2,
            ItemID.MINDRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_MIND,
            false,
            null
    ),
    WATER(
            StaticConstant.MINIGAME_WATER_OBELISK_ID,
            6,
            5,
            ItemID.WATERRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_WATER,
            false,
            null
    ),
    MIST_CRAFTED_ON_AIR(
            StaticConstant.MINIGAME_AIR_OBELISK_ID,
            8,
            6,
            ItemID.MISTRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_AIR,
            true,
            RuneCraftInfo.AIR
    ),
    MIST_CRAFTED_ON_WATER(
            StaticConstant.MINIGAME_WATER_OBELISK_ID,
            8.5,
            6,
            ItemID.MISTRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_WATER,
            true,
            RuneCraftInfo.WATER
    ),
    EARTH(
            StaticConstant.MINIGAME_EARTH_OBELISK_ID,
            6.5,
            9,
            ItemID.EARTHRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_EARTH,
            false,
            null
    ),
    DUST_CRAFTED_ON_AIR(
            StaticConstant.MINIGAME_AIR_OBELISK_ID,
            8.3,
            10,
            ItemID.DUSTRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_AIR,
            true,
            RuneCraftInfo.AIR
    ),
    DUST_CRAFTED_ON_EARTH(
            StaticConstant.MINIGAME_EARTH_OBELISK_ID,
            9,
            10,
            ItemID.DUSTRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_EARTH,
            true,
            RuneCraftInfo.EARTH
    ),
    MUD_CRAFTED_ON_WATER(
            StaticConstant.MINIGAME_WATER_OBELISK_ID,
            9.3,
            13,
            ItemID.MUDRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_WATER,
            true,
            RuneCraftInfo.WATER
    ),
    MUD_CRAFTED_ON_EARTH(
            StaticConstant.MINIGAME_EARTH_OBELISK_ID,
            9.5,
            13,
            ItemID.MUDRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_EARTH,
            true,
            RuneCraftInfo.EARTH
    ),
    FIRE(
            StaticConstant.MINIGAME_FIRE_OBELISK_ID,
            7,
            14,
            ItemID.FIRERUNE,
            StaticConstant.RUNECRAFTING_ALTAR_FIRE,
            false,
            null
    ),
    SMOKE_CRAFTED_ON_AIR(
            StaticConstant.MINIGAME_AIR_OBELISK_ID,
            8.5,
            15,
            ItemID.SMOKERUNE,
            StaticConstant.RUNECRAFTING_ALTAR_AIR,
            true,
            RuneCraftInfo.AIR
    ),
    SMOKE_CRAFTED_ON_FIRE(
            StaticConstant.MINIGAME_FIRE_OBELISK_ID,
            9.5,
            15,
            ItemID.SMOKERUNE,
            StaticConstant.RUNECRAFTING_ALTAR_FIRE,
            true,
            RuneCraftInfo.FIRE
    ),
    STEAM_CRAFTED_ON_WATER(
            StaticConstant.MINIGAME_WATER_OBELISK_ID,
            9.3,
            19,
            ItemID.STEAMRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_WATER,
            true,
            RuneCraftInfo.WATER
    ),
    STEAM_CRAFTED_ON_FIRE(
            StaticConstant.MINIGAME_FIRE_OBELISK_ID,
            10,
            19,
            ItemID.STEAMRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_FIRE,
            true,
            RuneCraftInfo.FIRE
    ),
    BODY(
            StaticConstant.MINIGAME_BODY_OBELISK_ID,
            7.5,
            20,
            ItemID.BODYRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_BODY,
            false,
            null
    ),
    LAVA_CRAFTED_ON_EARTH(
            StaticConstant.MINIGAME_EARTH_OBELISK_ID,
            10,
            23,
            ItemID.LAVARUNE,
            StaticConstant.RUNECRAFTING_ALTAR_EARTH,
            true,
            RuneCraftInfo.EARTH
    ),
    LAVA_CRAFTED_ON_FIRE(
            StaticConstant.MINIGAME_FIRE_OBELISK_ID,
            10.5,
            23,
            ItemID.LAVARUNE,
            StaticConstant.RUNECRAFTING_ALTAR_FIRE,
            true,
            RuneCraftInfo.FIRE
    ),
    COSMIC(
            StaticConstant.MINIGAME_COSMIC_OBELISK_ID,
            8,
            27,
            ItemID.COSMICRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_COSMIC,
            false,
            null
    ),
    CHAOS(
            StaticConstant.MINIGAME_CHAOS_OBELISK_ID,
            8.5,
            35,
            ItemID.CHAOSRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_CHAOS,
            false,
            null
    ),
    NATURE(
            StaticConstant.MINIGAME_NATURE_OBELISK_ID,
            9,
            44,
            ItemID.NATURERUNE,
            StaticConstant.RUNECRAFTING_ALTAR_NATURE,
            false,
            null
    ),
    LAW(
            StaticConstant.MINIGAME_LAW_OBELISK_ID,
            9.5,
            54,
            ItemID.LAWRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_LAW,
            false,
            null
    ),
    DEATH(
            StaticConstant.MINIGAME_DEATH_OBELISK_ID,
            10,
            65,
            ItemID.DEATHRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_DEATH,
            false,
            null
    ),
    BLOOD(
            StaticConstant.MINIGAME_BLOOD_OBELISK_ID,
            10.5,
            77,
            ItemID.BLOODRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_BLOOD,
            false,
            null
    ),
    AETHER_CRAFTED_ON_COSMIC(
            StaticConstant.MINIGAME_COSMIC_OBELISK_ID,
            20,
            90,
            ItemID.AETHERRUNE,
            StaticConstant.RUNECRAFTING_ALTAR_COSMIC,
            true,
            RuneCraftInfo.COSMIC
    );

    private final int guardianGameObjectId, levelRequired, spriteId, altarId;
    private final RuneCraftInfo baseRuneCraftInfo;
    private final boolean isCombinationRune;
    private final double baseExperience;

    RuneCraftInfo(
            int guardianGameObjectId,
            double baseExperience,
            int levelRequired,
            int spriteId,
            int altarId,
            boolean isCombinationRune,
            RuneCraftInfo baseRuneCraftInfo
    ) {
        this.guardianGameObjectId = guardianGameObjectId;
        this.baseRuneCraftInfo = baseRuneCraftInfo;
        this.isCombinationRune = isCombinationRune;
        this.baseExperience = baseExperience;
        this.levelRequired = levelRequired;
        this.spriteId = spriteId;
        this.altarId = altarId;
    }

    @Override
    public String toString() {
        return name().split("_")[0];
    }

    public static RuneCraftInfo find(GuardianOfTheRiftOptimizerConfig config, int altarId) {
        switch (altarId) {
            case StaticConstant.RUNECRAFTING_ALTAR_AIR:
                return config.airAltar().relation;
            case StaticConstant.RUNECRAFTING_ALTAR_MIND:
                return RuneCraftInfo.MIND;
            case StaticConstant.RUNECRAFTING_ALTAR_WATER:
                return config.waterAltar().relation;
            case StaticConstant.RUNECRAFTING_ALTAR_EARTH:
                return config.earthAltar().relation;
            case StaticConstant.RUNECRAFTING_ALTAR_FIRE:
                return config.fireAltar().relation;
            case StaticConstant.RUNECRAFTING_ALTAR_BODY:
                return RuneCraftInfo.BODY;
            case StaticConstant.RUNECRAFTING_ALTAR_COSMIC:
                return RuneCraftInfo.COSMIC;
            case StaticConstant.RUNECRAFTING_ALTAR_CHAOS:
                return RuneCraftInfo.CHAOS;
            case StaticConstant.RUNECRAFTING_ALTAR_NATURE:
                return RuneCraftInfo.NATURE;
            case StaticConstant.RUNECRAFTING_ALTAR_LAW:
                return RuneCraftInfo.LAW;
            case StaticConstant.RUNECRAFTING_ALTAR_DEATH:
                return RuneCraftInfo.DEATH;
            case StaticConstant.RUNECRAFTING_ALTAR_BLOOD:
                return RuneCraftInfo.BLOOD;
            default:
                return null;
        }
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
                return config.cosmicAltar().relation;
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

    public static final RuneCraftInfo[] VALUES = RuneCraftInfo.values();
}
