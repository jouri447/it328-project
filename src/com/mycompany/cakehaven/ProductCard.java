package com.mycompany.cakehaven;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.ParseException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * The card for ONE product: picture, name, price, stock, quantity selector and Add button.
 * It checks the quantity itself and only calls the AddHandler when the quantity is valid.
 *
 * STATUS: fully implemented.
 */
class ProductCard extends JPanel {

    /** Called by the card when the user clicked Add with a VALID quantity. */
    interface AddHandler {
        void onAdd(Product product, int quantity);
    }

    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = 150;
    private static final int LOW_STOCK_LIMIT = 5; // stock at or below this is shown in orange

    private final Product product;
    private final AddHandler addHandler;

    private final JLabel imageLabel = new JLabel();
    private final JLabel stockLabel = new JLabel();
    private final SpinnerNumberModel quantityModel = new SpinnerNumberModel(1, 1, 1, 1);
    private final JSpinner quantitySpinner = new JSpinner(quantityModel);
    private final RoundedButton addButton = new RoundedButton("Add", Theme.PINK, java.awt.Color.WHITE);

    ProductCard(Product product, AddHandler addHandler) {
        this.product = product;
        this.addHandler = addHandler;
        buildLayout();
        refresh();
    }

    public Product getProduct() {
        return product;
    }

    private void buildLayout() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setPreferredSize(new Dimension(236, 355));

        imageLabel.setIcon(ProductImages.load(product.getImagePath(), IMAGE_WIDTH, IMAGE_HEIGHT));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(product.getName(), SwingConstants.CENTER);
        nameLabel.setFont(Theme.NAME_FONT);
        nameLabel.setForeground(Theme.CHOCOLATE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(product.getFormattedPrice(), SwingConstants.CENTER);
        priceLabel.setFont(Theme.PRICE_FONT);
        priceLabel.setForeground(Theme.PINK);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        stockLabel.setFont(Theme.BADGE_FONT);
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Quantity row: "Quantity:" + spinner
        JPanel quantityRow = new JPanel();
        quantityRow.setOpaque(false);
        JLabel quantityText = new JLabel("Quantity:");
        quantityText.setFont(Theme.BODY_FONT);
        quantityText.setForeground(Theme.CHOCOLATE);
        quantitySpinner.setFont(Theme.BODY_FONT);
        quantitySpinner.setPreferredSize(new Dimension(70, 28));
        quantityRow.add(quantityText);
        quantityRow.add(quantitySpinner);
        quantityRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> handleAddClicked());

        add(imageLabel);
        add(javax.swing.Box.createVerticalStrut(12));
        add(nameLabel);
        add(javax.swing.Box.createVerticalStrut(4));
        add(priceLabel);
        add(javax.swing.Box.createVerticalStrut(8));
        add(stockLabel);
        add(javax.swing.Box.createVerticalStrut(12));
        add(quantityRow);
        add(javax.swing.Box.createVerticalStrut(10));
        add(addButton);
    }

    /**
     * Updates the card from the product's CURRENT stock.
     * Call this (on the Swing thread) whenever the stock changed.
     */
    void refresh() {
        int stock = product.getAvailableStock();
        boolean outOfStock = stock <= 0;

        if (outOfStock) {
            stockLabel.setText("OUT OF STOCK");
            stockLabel.setForeground(Theme.OUT_OF_STOCK);
            quantityModel.setMaximum(1);
            quantityModel.setValue(1);
            addButton.setText("Sold Out");
        } else {
            stockLabel.setText("In stock: " + stock);
            stockLabel.setForeground(stock <= LOW_STOCK_LIMIT ? Theme.LOW_STOCK : Theme.IN_STOCK);
            quantityModel.setMaximum(stock);
            // if the user had chosen more than what is left, lower the quantity to the new maximum
            if (((Integer) quantityModel.getValue()) > stock) {
                quantityModel.setValue(stock);
            }
            addButton.setText("Add");
        }

        // no stock -> the quantity selector and the Add button are disabled
        quantitySpinner.setEnabled(!outOfStock);
        addButton.setEnabled(!outOfStock);
        // a disabled JLabel shows its icon greyed out
        imageLabel.setEnabled(!outOfStock);
        addButton.revalidate();
        repaint();
    }

    /** Validates the quantity and, if it is valid, passes it on through the AddHandler. */
    private void handleAddClicked() {
        // 1. Read what the user typed (the spinner may still contain uncommitted text)
        try {
            quantitySpinner.commitEdit();
        } catch (ParseException e) {
            // typed text is not a whole number, or is outside 1..stock
            showWarning("Please enter a whole number between 1 and " + Math.max(1, product.getAvailableStock()) + ".");
            quantityModel.setValue(1);
            return;
        }
        int quantity = ((Number) quantitySpinner.getValue()).intValue();
        int stock = product.getAvailableStock();

        // 2. Validate against the CURRENT stock (it may have changed since the card was drawn)
        if (stock <= 0) {
            showWarning(product.getName() + " is out of stock.");
            refresh();
            return;
        }
        if (quantity < 1) {
            showWarning("The quantity must be at least 1.");
            quantityModel.setValue(1);
            return;
        }
        if (quantity > stock) {
            showWarning("Only " + stock + " of " + product.getName() + " available. Please choose a smaller quantity.");
            quantityModel.setValue(stock);
            return;
        }

        // 3. Valid -> tell the owner (ProductPanel), which forwards it to the cart component
        addHandler.onAdd(product, quantity);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                message, "Cake Haven", JOptionPane.WARNING_MESSAGE);
    }

    /** Draws the white rounded card background. */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.CARD);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
        g2.setColor(Theme.CARD_BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
        g2.dispose();
        super.paintComponent(g);
    }
}
