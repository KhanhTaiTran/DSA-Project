package animation;

import javax.swing.*;
import java.awt.*;
import Constants.Constants;

public class TileAnimation {
    public enum Type {
        MOVE, NEW, MERGE
    }

    private int startX, startY, endX, endY;
    private int value;
    private int duration; // in ms
    private long startTime;
    private boolean running = false;
    private Runnable repaintCallback;
    private Type type;

    public TileAnimation(int startX, int startY, int endX, int endY, int value, int duration,
            Runnable repaintCallback) {
        this(startX, startY, endX, endY, value, duration, repaintCallback, Type.MOVE);
    }

    public TileAnimation(int startX, int startY, int endX, int endY, int value, int duration,
            Runnable repaintCallback, Type type) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.value = value;
        this.duration = duration;
        this.repaintCallback = repaintCallback;
        this.type = type;
    }

    public void start() {
        running = true;
        startTime = System.currentTimeMillis();
        Timer timer = new Timer(1000 / 120, e -> { // 120 FPS for smoother animation
            long elapsed = System.currentTimeMillis() - startTime;
            if (!running) {
                ((Timer) e.getSource()).stop();
                return;
            }
            if (elapsed >= duration) {
                running = false;
                ((Timer) e.getSource()).stop();
            }
            repaintCallback.run();
        });
        timer.start();
    }

    public boolean isRunning() {
        return running;
    }

    public void paint(Graphics2D g2, int tileSize, int tileMargin) {
        if (!running)
            return;
        float rawProgress = Math.min(1.0f, (System.currentTimeMillis() - startTime) / (float) duration);
        // Ease-in-out for smoother animation
        float progress = (float) (0.5 - 0.5 * Math.cos(Math.PI * rawProgress));
        int x = (int) (startX + (endX - startX) * progress);
        int y = (int) (startY + (endY - startY) * progress);

        float scale = 1.0f;
        if (type == Type.NEW) {
            // Pop-in: scale from 0 to 1
            scale = progress;
        } else if (type == Type.MERGE) {
            // Pop: scale up to 1.2 then back to 1
            if (progress < 0.5f) {
                scale = 1.0f + 0.4f * progress * 2; // up to 1.4
            } else {
                scale = 1.4f - 0.4f * (progress - 0.5f) * 2; // back to 1
            }
        }
        int drawSize = (int) (tileSize * scale);
        int drawX = x + (tileSize - drawSize) / 2;
        int drawY = y + (tileSize - drawSize) / 2;

        g2.setColor(getTileColor(value));
        g2.fillRoundRect(drawX, drawY, drawSize, drawSize, 12, 12);
        g2.setColor(getTextColor(value));
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (36 * scale)));
        String s = String.valueOf(value);
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(s);
        int h = -(int) fm.getLineMetrics(s, g2).getBaselineOffsets()[2];
        g2.drawString(s, drawX + (drawSize - w) / 2, drawY + drawSize / 2 + h / 2);
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 0:
                return Constants.TILE_COLOR_EMPTY;
            case 2:
                return Constants.TILE_COLOR_2;
            case 4:
                return Constants.TILE_COLOR_4;
            case 8:
                return Constants.TILE_COLOR_8;
            case 16:
                return Constants.TILE_COLOR_16;
            case 32:
                return Constants.TILE_COLOR_32;
            case 64:
                return Constants.TILE_COLOR_64;
            case 128:
                return Constants.TILE_COLOR_128;
            case 256:
                return Constants.TILE_COLOR_256;
            case 512:
                return Constants.TILE_COLOR_512;
            case 1024:
                return Constants.TILE_COLOR_1024;
            case 2048:
                return Constants.TILE_COLOR_2048;
            default:
                return Constants.TILE_COLOR_SUPER;
        }
    }

    private Color getTextColor(int value) {
        if (value <= 4) {
            return Constants.TEXT_COLOR_LIGHT;
        } else {
            return Color.WHITE;
        }
    }
}
