package com.mycompany.cakehaven;

import java.awt.Color;
import java.awt.Font;

/**
 * Colours and fonts used by the Products screen, kept in one place so the
 * bakery look can be changed easily.
 *
 * STATUS: fully implemented.
 */
final class Theme {

    private Theme() {
    }

    // Bakery colours
    static final Color BACKGROUND   = new Color(0xFFF8F1); // warm cream
    static final Color HEADER       = new Color(0xFBE4EA); // soft pink
    static final Color PINK         = new Color(0xE0708E); // strawberry pink (main button colour)
    static final Color CHOCOLATE    = new Color(0x5A3A2E); // dark brown text
    static final Color MUTED_TEXT   = new Color(0x8C7468); // light brown text
    static final Color CARD         = Color.WHITE;
    static final Color CARD_BORDER  = new Color(0xEBD9CF);
    static final Color TAB_IDLE     = new Color(0xF3D3C4); // light caramel
    static final Color IN_STOCK     = new Color(0x3C8D5A); // green
    static final Color LOW_STOCK    = new Color(0xC77A1E); // orange
    static final Color OUT_OF_STOCK = new Color(0xC0392B); // red
    static final Color DISABLED_FILL = new Color(0xD9D2CC);
    static final Color DISABLED_TEXT = new Color(0x8A8580);

    // Fonts (if "Georgia" is missing, Java automatically uses a default font)
    static final Font TITLE_FONT    = new Font("Georgia", Font.BOLD, 38);
    static final Font SUBTITLE_FONT = new Font("Georgia", Font.ITALIC, 15);
    static final Font TAB_FONT      = new Font("SansSerif", Font.BOLD, 15);
    static final Font NAME_FONT     = new Font("Georgia", Font.BOLD, 17);
    static final Font PRICE_FONT    = new Font("SansSerif", Font.BOLD, 15);
    static final Font BODY_FONT     = new Font("SansSerif", Font.PLAIN, 14);
    static final Font BADGE_FONT    = new Font("SansSerif", Font.BOLD, 14);
}
