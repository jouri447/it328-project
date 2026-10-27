package com.mycompany.cakehaven;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 * A button with rounded corners that paints its own colours, so it looks the same
 * on Windows, macOS and Linux (normal Swing buttons ignore background colours
 * on some look-and-feels).
 *
 * STATUS: fully implemented.
 */
class RoundedButton extends JButton {

    private Color fillColor;
    private Color textColor;

    RoundedButton(String text, Color fillColor, Color textColor) {
        super(text);
        this.fillColor = fillColor;
        this.textColor = textColor;
        setFont(Theme.TAB_FONT);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /** Changes the colours (used by the category tabs to show which one is selected). */
    void setColors(Color fillColor, Color textColor) {
        this.fillColor = fillColor;
        this.textColor = textColor;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(getText()) + 36, fm.getHeight() + 14);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color fill;
        Color text;
        if (!isEnabled()) {
            fill = Theme.DISABLED_FILL;
            text = Theme.DISABLED_TEXT;
        } else {
            text = textColor;
            if (getModel().isPressed()) {
                fill = fillColor.darker();
            } else if (getModel().isRollover()) {
                fill = fillColor.brighter();
            } else {
                fill = fillColor;
            }
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

        // draw the label in the centre
        g2.setFont(getFont());
        g2.setColor(text);
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
