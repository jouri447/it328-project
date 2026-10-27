package com.mycompany.cakehaven;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Loads product pictures safely. If a picture is missing or broken, a simple
 * placeholder picture is drawn instead, so the application never crashes
 * because of an image.
 *
 * STATUS: fully implemented.
 */
final class ProductImages {

    // Pictures are loaded once and then reused (cache)
    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    private ProductImages() {
    }

    /**
     * Returns an icon of exactly width x height pixels (picture cropped to fit, rounded corners).
     * Never returns null and never throws.
     */
    static synchronized ImageIcon load(String path, int width, int height) {
        String key = path + "@" + width + "x" + height;
        ImageIcon cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        BufferedImage source = readImage(path);
        BufferedImage result;
        if (source == null) {
            result = createPlaceholder(width, height);
        } else {
            result = cropAndScale(source, width, height);
        }
        ImageIcon icon = new ImageIcon(result);
        CACHE.put(key, icon);
        return icon;
    }

    /** Tries the classpath first (images inside src), then a normal file path. Returns null on failure. */
    private static BufferedImage readImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        try {
            URL url = ProductImages.class.getResource(path);
            if (url != null) {
                return ImageIO.read(url);
            }
            File file = new File(path);
            if (file.isFile()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            // any problem reading the picture -> fall back to the placeholder
        }
        return null;
    }

    /** Scales the picture so it fills the target area, cutting off the overflow (like "cover"). */
    private static BufferedImage cropAndScale(BufferedImage src, int width, int height) {
        double scale = Math.max((double) width / src.getWidth(), (double) height / src.getHeight());
        int scaledW = (int) Math.ceil(src.getWidth() * scale);
        int scaledH = (int) Math.ceil(src.getHeight() * scale);
        Image scaled = src.getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);

        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new RoundRectangle2D.Float(0, 0, width, height, 18, 18));
        g2.drawImage(scaled, (width - scaledW) / 2, (height - scaledH) / 2, null);
        g2.dispose();
        return out;
    }

    /** A pink rounded rectangle with the text "Image coming soon". */
    private static BufferedImage createPlaceholder(int width, int height) {
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Theme.HEADER);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, 18, 18));
        g2.setColor(Theme.MUTED_TEXT);
        g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
        FontMetrics fm = g2.getFontMetrics();
        String text = "Image coming soon";
        g2.drawString(text, (width - fm.stringWidth(text)) / 2, height / 2 + fm.getAscent() / 2);
        g2.dispose();
        return out;
    }
}
