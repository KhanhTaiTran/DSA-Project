package animation;

import javax.swing.*;
import java.awt.*;

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
                return new Color(205, 193, 180);
            case 2:
                return new Color(238, 228, 218);
            case 4:
                return new Color(237, 224, 200);
            case 8:
                return new Color(242, 177, 121);
            case 16:
                return new Color(245, 149, 99);
            case 32:
                return new Color(246, 124, 95);
            case 64:
                return new Color(246, 94, 59);
            case 128:
                return new Color(237, 207, 114);
            case 256:
                return new Color(237, 204, 97);
            case 512:
                return new Color(237, 200, 80);
            case 1024:
                return new Color(237, 197, 63);
            case 2048:
                return new Color(237, 194, 46);
            default:
                return new Color(60, 58, 50);
        }
    }

    private Color getTextColor(int value) {
        if (value <= 4) {
            return new Color(119, 110, 101);
        } else {
            return Color.WHITE;
        }
    }
}
