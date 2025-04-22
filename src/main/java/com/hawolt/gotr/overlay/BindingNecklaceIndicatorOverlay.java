package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.utility.ImageOutline;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.util.ImageUtil;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.ScheduledExecutorService;

public class BindingNecklaceIndicatorOverlay extends AbstractMinigameRenderer {

    @Inject
    private ItemManager itemManager;

    @Inject
    private ScheduledExecutorService executor;

    @Getter(AccessLevel.NONE)
    private final int SPRITE_DIMENSION_HEIGHT = 32;

    @Getter(AccessLevel.NONE)
    private final int SPRITE_DIMENSION_WIDTH = 32;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private boolean isMagicImbueActive;

    @Getter(AccessLevel.NONE)
    private BufferedImage blank;

    @Inject
    public BindingNecklaceIndicatorOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public void startup() {
        super.startup();
        this.executor.execute(()->{
            try (InputStream inputStream = BindingNecklaceIndicatorOverlay.class.getResourceAsStream("/blank_sprite.png")) {
                if (inputStream == null) return;
                synchronized (ImageIO.class){
                    this.blank = ImageIO.read(inputStream);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @Override
    public void shutdown() {
        this.blank = null;
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged changed) {
        if (changed.getVarbitId() != VarbitID.MAGIC_IMBUE_ACTIVE) return;
        this.isMagicImbueActive = changed.getValue() > 0;
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (blank == null) return null;
        if (getRenderSafetyEvent() == null) return null;
        if (!getRenderSafetyEvent().isWidgetAvailable() || getRenderSafetyEvent().isVolatileState()) return null;
        this.renderWhenSecure(graphics2D);
        return null;
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        if (!plugin.getConfig().isShowBindingNecklaceStatus()) return;

        Widget parentWidget = plugin.getClient().getWidget(StaticConstant.MINIGAME_WIDGET_PARENT_ID);
        Widget guardianWidget = plugin.getClient().getWidget(StaticConstant.MINIGAME_WIDGET_GUARDIAN_ID);

        if (parentWidget == null || guardianWidget == null) return;
        if (parentWidget.isHidden() || guardianWidget.isHidden()) return;


        BufferedImage sprite = itemManager.getImage(StaticConstant.BINDING_NECKLACE_ITEM_ID);
        if (sprite == null) return;

        sprite = !plugin.getEquipmentSlice().isBindingNecklaceEquipped() ?
                ImageUtil.grayscaleImage(sprite) :
                sprite;

        int spriteLocationX = parentWidget.getRelativeX() + guardianWidget.getRelativeX() + guardianWidget.getWidth() + 16;
        int spriteLocationY = parentWidget.getRelativeY() + guardianWidget.getRelativeY() + 12;

        if (isMagicImbueActive) {
            graphics2D.drawImage(
                    ImageOutline.create(blank, Color.CYAN),
                    spriteLocationX - 1,
                    spriteLocationY - 1,
                    null
            );
        }

        graphics2D.drawImage(
                blank,
                spriteLocationX,
                spriteLocationY,
                null
        );
        graphics2D.drawImage(
                sprite,
                spriteLocationX,
                spriteLocationY,
                null
        );

        Rectangle bounds = new Rectangle(
                spriteLocationX,
                spriteLocationY + SPRITE_DIMENSION_HEIGHT + 1,
                SPRITE_DIMENSION_WIDTH,
                24);

        int charges = plugin.getBindingNecklaceSlice().getBindingNecklaceCharges();
        String text = String.format(
                "%s/%s",
                charges <= 0 ? "?" : charges,
                16
        );

        this.drawStringCenteredToBoundingBox(
                graphics2D,
                bounds,
                text,
                Color.WHITE
        );

    }
}
