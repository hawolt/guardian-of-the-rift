package com.hawolt.gotr;

import com.hawolt.gotr.data.RuneCraftInfo;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("guardiansOfTheRiftOptimizer")
public interface GuardianOfTheRiftOptimizerConfig extends Config {

    @ConfigSection(
            position = 0,
            name = "Rune Configuration",
            description = "Configure which runes you are crafting on the elemental altars"
    )
    String combinationRuneConfiguration = "combinationRuneConfiguration";

    enum AirAlterOptionEnum {
        AIR(RuneCraftInfo.AIR),
        MIST(RuneCraftInfo.MIST_CRAFTED_ON_AIR),
        DUST(RuneCraftInfo.DUST_CRAFTED_ON_AIR),
        SMOKE(RuneCraftInfo.SMOKE_CRAFTED_ON_AIR);
        public final RuneCraftInfo relation;

        AirAlterOptionEnum(final RuneCraftInfo relation) {
            this.relation = relation;
        }
    }

    @ConfigItem(
            position = 0,
            keyName = "airAltar",
            name = "Air Altar",
            description = "The rune to be crafted",
            section = combinationRuneConfiguration
    )
    default AirAlterOptionEnum airAltar() {
        return AirAlterOptionEnum.AIR;
    }

    enum WaterAlterOptionEnum {
        WATER(RuneCraftInfo.WATER),
        MIST(RuneCraftInfo.MIST_CRAFTED_ON_WATER),
        MUD(RuneCraftInfo.MUD_CRAFTED_ON_WATER),
        STEAM(RuneCraftInfo.STEAM_CRAFTED_ON_WATER);
        public final RuneCraftInfo relation;

        WaterAlterOptionEnum(final RuneCraftInfo relation) {
            this.relation = relation;
        }
    }

    @ConfigItem(
            position = 1,
            keyName = "waterAltar",
            name = "Water Altar",
            description = "The rune to be crafted",
            section = combinationRuneConfiguration
    )
    default WaterAlterOptionEnum waterAltar() {
        return WaterAlterOptionEnum.WATER;
    }

    enum EarthAlterOptionEnum {
        EARTH(RuneCraftInfo.EARTH),
        DUST(RuneCraftInfo.DUST_CRAFTED_ON_EARTH),
        MUD(RuneCraftInfo.MUD_CRAFTED_ON_EARTH),
        LAVA(RuneCraftInfo.LAVA_CRAFTED_ON_EARTH);
        public final RuneCraftInfo relation;

        EarthAlterOptionEnum(final RuneCraftInfo relation) {
            this.relation = relation;
        }
    }

    @ConfigItem(
            position = 2,
            keyName = "earthAltar",
            name = "Earth Altar",
            description = "The rune to be crafted",
            section = combinationRuneConfiguration
    )
    default EarthAlterOptionEnum earthAltar() {
        return EarthAlterOptionEnum.EARTH;
    }

    enum FireAlterOptionEnum {
        FIRE(RuneCraftInfo.FIRE),
        SMOKE(RuneCraftInfo.SMOKE_CRAFTED_ON_FIRE),
        STEAM(RuneCraftInfo.STEAM_CRAFTED_ON_FIRE),
        LAVA(RuneCraftInfo.LAVA_CRAFTED_ON_FIRE);
        public final RuneCraftInfo relation;

        FireAlterOptionEnum(final RuneCraftInfo relation) {
            this.relation = relation;
        }
    }

    @ConfigItem(
            position = 3,
            keyName = "fireAltar",
            name = "Fire Altar",
            description = "The rune to be crafted",
            section = combinationRuneConfiguration
    )
    default FireAlterOptionEnum fireAltar() {
        return FireAlterOptionEnum.FIRE;
    }

    @ConfigSection(
            position = 1,
            name = "Optimal Guardian",
            description = "Customize the outline for the Optimal Guardian"
    )
    String optimalGuardianConfiguration = "optimalGuardianConfiguration";

    @ConfigItem(
            keyName = "optimalGuardianTimerEnabled",
            name = "Guardian Despawn Timer",
            description = "Whether to draw a timer for the Guardian Despawn",
            position = 0,
            section = optimalGuardianConfiguration
    )
    default boolean isOptimalGuardianTimerEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "optimalGuardianSpriteEnabled",
            name = "Guardian Rune Sprite",
            description = "Whether to draw the according Rune Sprite",
            position = 1,
            section = optimalGuardianConfiguration
    )
    default boolean isOptimalGuardianSpriteEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "optimalGuardianRunTime",
            name = "Show Time To Guardian",
            description = "Will show you how much time is required to reach the Guardian",
            position = 2,
            section = optimalGuardianConfiguration
    )
    default boolean isOptimalGuardianRunTime() {
        return true;
    }

    @ConfigItem(
            keyName = "optimalGuardianOutlineEnabled",
            name = "Guardian Outline",
            description = "Whether to draw an outline or not",
            position = 3,
            section = optimalGuardianConfiguration
    )
    default boolean isOptimalGuardianOutlineEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "optimalGuardianOutlineColor",
            name = "Outline",
            description = "Color of the outline",
            position = 4,
            section = optimalGuardianConfiguration
    )
    default Color optimalGuardianOutlineColor() {
        return new Color(51, 255, 153, 255);
    }


    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "optimalGuardianOutlineWidth",
            name = "Outline width",
            description = "Width for the outline to be drawn",
            position = 5,
            section = optimalGuardianConfiguration
    )
    default int optimalGuardianOutlineWidth() {
        return 5;
    }

    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "optimalGuardianOutlineFeatherDistance",
            name = "Feather Distance",
            description = "Smoothing distance for the outline",
            position = 6,
            section = optimalGuardianConfiguration
    )
    default int optimalGuardianOutlineFeatherDistance() {
        return 3;
    }

    @ConfigSection(
            position = 2,
            name = "Secondary Guardian",
            description = "Customize the outline for the Secondary Guardian"
    )
    String secondaryGuardianConfiguration = "secondaryGuardianConfiguration";

    @ConfigItem(
            keyName = "secondaryGuardianTimerEnabled",
            name = "Guardian Despawn Timer",
            description = "Whether to draw a timer for the Guardian Despawn",
            position = 0,
            section = secondaryGuardianConfiguration
    )
    default boolean isSecondaryGuardianTimerEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "secondaryGuardianSpriteEnabled",
            name = "Guardian Rune Sprite",
            description = "Whether to draw the according Rune Sprite",
            position = 1,
            section = secondaryGuardianConfiguration
    )
    default boolean isSecondaryGuardianSpriteEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "secondaryGuardianRunTime",
            name = "Show Time To Guardian",
            description = "Will show you how much time is required to reach the Guardian",
            position = 2,
            section = secondaryGuardianConfiguration
    )
    default boolean isSecondaryGuardianRunTime() {
        return true;
    }

    @ConfigItem(
            keyName = "secondaryGuardianOutlineEnabled",
            name = "Guardian Outline",
            description = "Whether to draw an outline or not",
            position = 3,
            section = secondaryGuardianConfiguration
    )
    default boolean isSecondaryGuardianOutlineEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "secondaryGuardianOutlineColor",
            name = "Outline",
            description = "Color of the outline",
            position = 4,
            section = secondaryGuardianConfiguration
    )
    default Color secondaryGuardianOutlineColor() {
        return new Color(255, 255, 255, 204);
    }


    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "secondaryGuardianOutlineWidth",
            name = "Outline width",
            description = "Width for the outline to be drawn",
            position = 5,
            section = secondaryGuardianConfiguration
    )
    default int secondaryGuardianOutlineWidth() {
        return 3;
    }

    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "secondaryGuardianOutlineFeatherDistance",
            name = "Feather Distance",
            description = "Smoothing distance for the outline",
            position = 6,
            section = secondaryGuardianConfiguration
    )
    default int secondaryGuardianOutlineFeatherDistance() {
        return 5;
    }

    @ConfigSection(
            position = 3,
            name = "Menu Swapping",
            description = "Hide specific Menu Options if conditions match"
    )
    String menuSwappingConfiguration = "menuSwappingConfiguration";

    @ConfigItem(
            keyName = "hideApprenticeTalkTo",
            name = "Hide Talk-to",
            description = "Hides the left click Talk-to option on the Apprentices",
            position = 0,
            section = menuSwappingConfiguration
    )
    default boolean isHideApprenticeTalkTo() {
        return true;
    }

    @ConfigItem(
            keyName = "hideGuardianPowerUp",
            name = "Hide Power-Up",
            description = "Hides the left click Power-up option when there is no essence in the inventory",
            position = 1,
            section = menuSwappingConfiguration
    )
    default boolean isHideGuardianPowerUp() {
        return true;
    }

    @ConfigItem(
            keyName = "hideGroundItemPlaceCell",
            name = "Hide Place-cell",
            description = "Hides the left click Place-cell option when there is no cell in the inventory",
            position = 2,
            section = menuSwappingConfiguration
    )
    default boolean isHideGroundItemPlaceCell() {
        return true;
    }

    @ConfigItem(
            keyName = "hideGuardianAssembleNoMaterial",
            name = "Hide Assemble (Chisel/Cell)",
            description = "Hides the left click Assemble option if you have no Chisel or Cell",
            position = 3,
            section = menuSwappingConfiguration
    )
    default boolean isHideGuardianAssembleNoMaterial() {
        return true;
    }

    @ConfigItem(
            keyName = "hideGuardianAssembleAllActive",
            name = "Hide Assemble (10 Guardian)",
            description = "Hides the left click Assemble option if all Guardians are active",
            position = 4,
            section = menuSwappingConfiguration
    )
    default boolean isHideGuardianAssembleAllActive() {
        return true;
    }

    @ConfigItem(
            keyName = "hideUseOptionOnPlayer",
            name = "Hide Use on Player for Runes",
            description = "Hides the Use Option on other Players for any rune",
            position = 5,
            section = menuSwappingConfiguration
    )
    default boolean isHideUseOptionOnPlayer() {
        return true;
    }

    @ConfigItem(
            keyName = "hideDepositPoolDepositOption",
            name = "Hide Deposit (Deposit Pool)",
            description = "Hides the left click Deposit option",
            position = 6,
            section = menuSwappingConfiguration
    )
    default boolean isHideDepositPoolDepositOption() {
        return false;
    }

    @ConfigSection(
            position = 4,
            name = "Miscellaneous",
            description = "Various Quality of Life options"
    )
    String miscellaneousConfiguration = "miscellaneousConfiguration";

    @ConfigItem(
            keyName = "hideApprenticeHelpMessages",
            name = "Mute Apprentice Instructions",
            description = "Mutes help messages from the Apprentices",
            position = 0,
            section = miscellaneousConfiguration
    )
    default boolean isHideApprenticeHelpMessages() {
        return true;
    }

    @ConfigItem(
            keyName = "showGameStartTimer",
            name = "Game Start Timer",
            description = "Replace Guardian Power Bar with a Game Start timer when applicable",
            position = 1,
            section = miscellaneousConfiguration
    )
    default boolean isShowGameStartTimer() {
        return true;
    }

    @ConfigItem(
            keyName = "showTimeSinceLastPortal",
            name = "Time since last Portal",
            description = "Display the time that has elapsed since the last Portal has despawned",
            position = 2,
            section = miscellaneousConfiguration
    )
    default boolean isShowTimeSinceLastPortal() {
        return true;
    }

    @ConfigItem(
            keyName = "showBindingNecklaceStatus",
            name = "Binding Necklace Info",
            description = "Display an indicator whether your Binding Necklace is equipped or not and its charges",
            position = 3,
            section = miscellaneousConfiguration
    )
    default boolean isShowBindingNecklaceStatus() {
        return true;
    }

    @ConfigItem(
            keyName = "showPointStatusInfobox",
            name = "Point Status Infobox",
            description = "Display an additional Infobox with a minigame Point Status",
            position = 4,
            section = miscellaneousConfiguration
    )
    default boolean isShowPointStatusInfobox() {
        return true;
    }

    @ConfigSection(
            position = 5,
            name = "The Great Guardian",
            description = "Options to enhance The Great Guardian"
    )
    String theGreatGuardianConfiguration = "theGreatGuardianConfiguration";

    @ConfigItem(
            keyName = "theGreatGuardianOutlineEnabled",
            name = "The Great Guardian Outline",
            description = "Whether to draw an outline or not",
            position = 3,
            section = theGreatGuardianConfiguration
    )
    default boolean isTheGreatGuardianOutlineEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "theGreatGuardianOutlineColor",
            name = "Outline",
            description = "Color of the outline",
            position = 4,
            section = theGreatGuardianConfiguration
    )
    default Color theGreatGuardianOutlineColor() {
        return new Color(255, 255, 255, 204);
    }


    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "theGreatGuardianOutlineWidth",
            name = "Outline width",
            description = "Width for the outline to be drawn",
            position = 5,
            section = theGreatGuardianConfiguration
    )
    default int theGreatGuardianOutlineWidth() {
        return 3;
    }

    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "theGreatGuardianOutlineFeatherDistance",
            name = "Feather Distance",
            description = "Smoothing distance for the outline",
            position = 6,
            section = theGreatGuardianConfiguration
    )
    default int theGreatGuardianOutlineFeatherDistance() {
        return 5;
    }

    @ConfigSection(
            position = 6,
            name = "Cell Tiles",
            description = "Options to enhance the Cell Tiles"
    )
    String cellTileConfiguration = "cellTileConfiguration";

    @ConfigItem(
            keyName = "cellTilesOutlineEnabled",
            name = "Cell Tile Outline",
            description = "Whether to draw an outline or not",
            position = 3,
            section = cellTileConfiguration
    )
    default boolean isCellTilesOutlineEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "cellTilesOutlineColor",
            name = "Outline",
            description = "Color of the outline",
            position = 4,
            section = cellTileConfiguration
    )
    default Color cellTilesOutlineColor() {
        return new Color(255, 255, 255, 204);
    }


    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "cellTilesOutlineWidth",
            name = "Outline width",
            description = "Width for the outline to be drawn",
            position = 5,
            section = cellTileConfiguration
    )
    default int cellTilesOutlineWidth() {
        return 3;
    }

    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "cellTilesOutlineFeatherDistance",
            name = "Feather Distance",
            description = "Smoothing distance for the outline",
            position = 6,
            section = cellTileConfiguration
    )
    default int cellTilesOutlineFeatherDistance() {
        return 5;
    }

    @ConfigSection(
            position = 7,
            name = "Uncharged Cell Table",
            description = "Options to enhance the Uncharged Cell Table"
    )
    String unchargedCellTableConfiguration = "unchargedCellTableConfiguration";

    @ConfigItem(
            keyName = "cellTableOutlineEnabled",
            name = "Cell Table Outline",
            description = "Whether to draw an outline or not",
            position = 3,
            section = unchargedCellTableConfiguration
    )
    default boolean isCellTableOutlineEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "cellTableOutlineColor",
            name = "Outline",
            description = "Color of the outline",
            position = 4,
            section = unchargedCellTableConfiguration
    )
    default Color cellTableOutlineColor() {
        return new Color(255, 255, 255, 204);
    }


    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "cellTableOutlineWidth",
            name = "Outline width",
            description = "Width for the outline to be drawn",
            position = 5,
            section = unchargedCellTableConfiguration
    )
    default int cellTableOutlineWidth() {
        return 3;
    }

    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "cellTableOutlineFeatherDistance",
            name = "Feather Distance",
            description = "Smoothing distance for the outline",
            position = 6,
            section = unchargedCellTableConfiguration
    )
    default int cellTableOutlineFeatherDistance() {
        return 5;
    }

    @ConfigSection(
            position = 8,
            name = "Portal",
            description = "Options to enhance the Portal"
    )
    String portalConfiguration = "portalConfiguration";

    @ConfigItem(
            keyName = "portalOutlineEnabled",
            name = "Portal Outline",
            description = "Whether to draw an outline or not",
            position = 1,
            section = portalConfiguration
    )
    default boolean isPortalOutlineEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "portalTimeRemainingEnabled",
            name = "Portal Time Remaining",
            description = "Display time until Portal will despawn",
            position = 1,
            section = portalConfiguration
    )
    default boolean isPortalTimeRemainingEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "portalRunTimeEnabled",
            name = "Portal Distance Time",
            description = "Display time needed to reach the Portal",
            position = 2,
            section = portalConfiguration
    )
    default boolean isPortalRunTimeEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "portalOutlineColor",
            name = "Outline",
            description = "Color of the outline",
            position = 3,
            section = portalConfiguration
    )
    default Color portalOutlineColor() {
        return new Color(255, 255, 255, 204);
    }


    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "portalOutlineWidth",
            name = "Outline width",
            description = "Width for the outline to be drawn",
            position = 4,
            section = portalConfiguration
    )
    default int portalOutlineWidth() {
        return 3;
    }

    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "portalOutlineFeatherDistance",
            name = "Feather Distance",
            description = "Smoothing distance for the outline",
            position = 5,
            section = portalConfiguration
    )
    default int portalOutlineFeatherDistance() {
        return 5;
    }

    @ConfigSection(
            position = 9,
            name = "Deposit Pool",
            description = "Options to enhance the Deposit Pool"
    )
    String depositPoolConfiguration = "depositPoolConfiguration";


    @ConfigItem(
            keyName = "depositPoolOutlineEnabled",
            name = "Deposit Pool Outline",
            description = "Whether to draw an outline or not",
            position = 1,
            section = depositPoolConfiguration
    )
    default boolean isDepositPoolOutlineEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(
            keyName = "depositPoolOutlineColor",
            name = "Outline",
            description = "Color of the outline",
            position = 2,
            section = depositPoolConfiguration
    )
    default Color depositPoolOutlineColor() {
        return new Color(255, 255, 255, 204);
    }


    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "depositPoolOutlineWidth",
            name = "Outline width",
            description = "Width for the outline to be drawn",
            position = 3,
            section = depositPoolConfiguration
    )
    default int depositPoolOutlineWidth() {
        return 3;
    }

    @Range(
            min = 1,
            max = 5
    )
    @ConfigItem(
            keyName = "depositPoolOutlineFeatherDistance",
            name = "Feather Distance",
            description = "Smoothing distance for the outline",
            position = 4,
            section = depositPoolConfiguration
    )
    default int depositPoolOutlineFeatherDistance() {
        return 5;
    }

    @ConfigSection(
            position = 10,
            name = "Debugging",
            description = "Options to display additional information"
    )
    String debugConfiguration = "debugConfiguration";

    @ConfigItem(
            position = 0,
            keyName = "enableInternalWeightDebugging",
            name = "Internal Weight",
            description = "Toggle internal weight debugging",
            section = debugConfiguration
    )
    default boolean enableWeightDebugging() {
        return false;
    }

    @ConfigItem(
            position = 1,
            keyName = "enableOptimalPathDebugging",
            name = "Path to Optimal Guardian",
            description = "Toggle Optimal Path debugging",
            section = debugConfiguration
    )
    default boolean enableOptimalPathDebugging() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "enableSecondaryPathDebugging",
            name = "Path to Secondary Guardian",
            description = "Toggle Secondary Path debugging",
            section = debugConfiguration
    )
    default boolean enableSecondaryPathDebugging() {
        return false;
    }

    @Alpha
    @ConfigItem(
            keyName = "tileOutlineColor",
            name = "Tile Outline",
            description = "Color of the outline",
            position = 3,
            section = debugConfiguration
    )
    default Color tileOutlineColor() {
        return new Color(255, 255, 255, 179);
    }
}
