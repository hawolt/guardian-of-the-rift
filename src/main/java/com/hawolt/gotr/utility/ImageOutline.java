package com.hawolt.gotr.utility;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageOutline {
    public static BufferedImage create(BufferedImage image, Color outlineColor) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage padded = new BufferedImage(width + 2, height + 2, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;
                if (alpha > 0) {
                    padded.setRGB(x + 1, y + 1, pixel);
                }
            }
        }

        boolean[][] isColored = new boolean[width + 2][height + 2];
        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                int alpha = (padded.getRGB(x, y) >> 24) & 0xff;
                if (alpha > 0) {
                    isColored[x][y] = true;
                }
            }
        }
        for (int y = 1; y <= height; y++) {
            for (int x = 1; x <= width; x++) {
                if (isColored[x][y]) {
                    togglePixel(padded, x, y, outlineColor);
                    togglePixel(padded, x - 1, y, outlineColor);
                    togglePixel(padded, x + 1, y, outlineColor);
                    togglePixel(padded, x, y - 1, outlineColor);
                    togglePixel(padded, x, y + 1, outlineColor);
                }
            }
        }

        return padded;
    }

    private static void togglePixel(BufferedImage image, int x, int y, Color color) {
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) return;
        image.setRGB(x, y, color.getRGB());
    }
}
