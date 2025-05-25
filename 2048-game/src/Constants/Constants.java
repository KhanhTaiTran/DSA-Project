package Constants;

import java.awt.Color;

public class Constants {
    // Window dimensions
    public static final int WIDTH = 500;
    public static final int HEIGHT = 600;

    public static final int SIZE = 4; // Size of the grid
    public static final int TILE_SIZE = 100; // Size of each tile
    public static final int TILE_MARGIN = 15; // Margin between tiles

    // Tile colors
    public static final Color TILE_COLOR_2 = new Color(238, 228, 218);
    public static final Color TILE_COLOR_4 = new Color(237, 224, 200);
    public static final Color TILE_COLOR_8 = new Color(242, 177, 121);
    public static final Color TILE_COLOR_16 = new Color(245, 149, 99);
    public static final Color TILE_COLOR_32 = new Color(246, 124, 95);
    public static final Color TILE_COLOR_64 = new Color(246, 94, 59);
    public static final Color TILE_COLOR_128 = new Color(237, 207, 114);
    public static final Color TILE_COLOR_256 = new Color(237, 204, 97);
    public static final Color TILE_COLOR_512 = new Color(237, 200, 80);
    public static final Color TILE_COLOR_1024 = new Color(237, 197, 63);
    public static final Color TILE_COLOR_2048 = new Color(237, 194, 46);
    public static final Color TILE_COLOR_SUPER = new Color(60, 58, 50);
    public static final Color TILE_COLOR_EMPTY = new Color(205, 193, 180);

    // Array of tile colors for easy access by index
    public static final Color[] TILE_COLORS = {
            TILE_COLOR_2,
            TILE_COLOR_4,
            TILE_COLOR_8,
            TILE_COLOR_16,
            TILE_COLOR_32,
            TILE_COLOR_64,
            TILE_COLOR_128,
            TILE_COLOR_256,
            TILE_COLOR_512,
            TILE_COLOR_1024,
            TILE_COLOR_2048
    };

    // Text colors
    public static final Color TEXT_COLOR_LIGHT = new Color(119, 110, 101);
    public static final Color TEXT_COLOR_DARK = Color.WHITE;

    // Background colors
    public static final Color BACKGROUND_LIGHT = new Color(250, 248, 239);
    public static final Color BACKGROUND_MEDIUM = new Color(240, 226, 210);
    public static final Color BOARD_COLOR = new Color(187, 173, 160);

    // Button colors
    public static final Color BUTTON_COLOR = new Color(242, 177, 121);
    public static final Color BUTTON_HOVER_COLOR = new Color(245, 149, 99);

    // Animation
    public static final int ANIMATION_SPEED = 50;
}
