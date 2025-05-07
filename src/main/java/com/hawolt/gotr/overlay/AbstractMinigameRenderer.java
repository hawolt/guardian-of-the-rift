package com.hawolt.gotr.overlay;

import com.hawolt.gotr.Slice;
import com.hawolt.gotr.events.RenderSafetyEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;

public abstract class AbstractMinigameRenderer extends Overlay implements Slice {
    @Inject
    protected EventBus bus;

    @Getter(AccessLevel.PROTECTED)
    private RenderSafetyEvent renderSafetyEvent;

    @Override
    public void startup() {
        this.bus.register(this);
    }

    @Override
    public void shutdown() {
        this.bus.unregister(this);
    }

    @Override
    public boolean isClientThreadRequiredOnStartup() {
        return false;
    }

    @Override
    public boolean isClientThreadRequiredOnShutDown() {
        return false;
    }

    @Subscribe
    public void onRenderSafetyEvent(RenderSafetyEvent event) {
        this.renderSafetyEvent = event;
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (renderSafetyEvent == null) return null;
        if (!renderSafetyEvent.isInGame() || renderSafetyEvent.isVolatileState()) return null;
        this.renderWhenSecure(graphics2D);
        return null;
    }

    public abstract void renderWhenSecure(Graphics2D graphics2D);

    protected void drawStringCenteredToBoundingBox(Graphics2D graphics2D, Rectangle bounds, String text, Color color) {
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(text);
        int textLocationX = bounds.x + ((bounds.width - stringWidth) >> 1);
        int textLocationY = bounds.y + ((bounds.height - fontMetrics.getHeight()) >> 1) + fontMetrics.getAscent();
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawString(text, textLocationX + 1, textLocationY + 1);
        graphics2D.setColor(color);
        graphics2D.drawString(text, textLocationX, textLocationY);
    }
}
