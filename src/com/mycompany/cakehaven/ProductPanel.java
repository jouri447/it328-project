package com.mycompany.cakehaven;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * The Products & Categories screen: Cake Haven title, three category tabs and the product cards.
 * It can be placed inside any JFrame or other container (it is a normal JPanel).
 *
 * How it connects to the rest of the project:
 *  - Product data/stock  <-  ProductRepository (the server code updates the repository,
 *                            or simply calls updateProductStock(...) on this panel)
 *  - "Add" clicks        ->  ProductSelectionListener (implemented by the cart component)
 *
 * STATUS: fully implemented. Nothing in this class talks to a server or to a cart class.
 */
public class ProductPanel extends JPanel implements ProductRepositoryListener {

    private static final int COLUMNS = 4;

    private final ProductRepository repository;
    private ProductSelectionListener selectionListener; // set later by the cart component

    private final Map<String, ProductCard> cardsByProductId = new HashMap<>();
    private final Map<Category, RoundedButton> tabButtons = new HashMap<>();
    private final CardLayout categoryLayout = new CardLayout();
    private final JPanel categoryHolder = new JPanel(categoryLayout);
    private final JLabel statusLabel = new JLabel(" ");
    private Category selectedCategory = Category.CAKES;

    /**
     * Builds the screen from the products currently in the repository.
     * Create this on the Event Dispatch Thread (see StoreFrame).
     */
    public ProductPanel(ProductRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        this.repository = repository;

        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        rebuildProductCards();
        repository.addListener(this); // be told when stock or products change
    }

    // ------------------------------------------------------------------
    // PUBLIC API for teammates
    // ------------------------------------------------------------------

    /**
     * [INTEGRATION POINT - CART] Registers the object that receives (productId, quantity)
     * when the user clicks Add. Pass null to remove it.
     */
    public void setProductSelectionListener(ProductSelectionListener listener) {
        this.selectionListener = listener;
    }

    /**
     * [INTEGRATION POINT - SERVER] Call this when a stock update arrives from the server.
     * It can be called from ANY thread (for example the client's network thread).
     *
     * @return true if the product exists and the stock was updated
     */
    public boolean updateProductStock(String productId, int newStock) {
        return repository.updateStock(productId, newStock);
    }

    /** Shows the given category tab (the cards of the other categories are hidden). */
    public void showCategory(Category category) {
        if (category == null) {
            return;
        }
        selectedCategory = category;
        categoryLayout.show(categoryHolder, category.name());
        for (Category c : tabButtons.keySet()) {
            boolean selected = (c == category);
            tabButtons.get(c).setColors(selected ? Theme.PINK : Theme.TAB_IDLE,
                    selected ? java.awt.Color.WHITE : Theme.CHOCOLATE);
        }
        statusLabel.setText(" ");
    }

    // ------------------------------------------------------------------
    // ProductRepositoryListener (may be called from any thread -> use invokeLater)
    // ------------------------------------------------------------------

    @Override
    public void onStockChanged(Product product) {
        SwingUtilities.invokeLater(() -> {
            ProductCard card = cardsByProductId.get(product.getId());
            if (card != null) {
                card.refresh();
            }
        });
    }

    @Override
    public void onProductsReloaded() {
        SwingUtilities.invokeLater(this::rebuildProductCards);
    }

    // ------------------------------------------------------------------
    // Building the screen
    // ------------------------------------------------------------------

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Theme.HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(18, 20, 14, 20));

        JLabel title = new JLabel("Cake Haven", SwingConstants.CENTER);
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.CHOCOLATE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Freshly baked cakes, cupcakes and pastries", SwingConstants.CENTER);
        subtitle.setFont(Theme.SUBTITLE_FONT);
        subtitle.setForeground(Theme.MUTED_TEXT);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        // One tab button per category
        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        tabs.setOpaque(false);
        tabs.setAlignmentX(CENTER_ALIGNMENT);
        for (Category category : Category.values()) {
            RoundedButton tab = new RoundedButton(category.getDisplayName(), Theme.TAB_IDLE, Theme.CHOCOLATE);
            tab.addActionListener(e -> showCategory(category));
            tabButtons.put(category, tab);
            tabs.add(tab);
        }

        header.add(title);
        header.add(javax.swing.Box.createVerticalStrut(2));
        header.add(subtitle);
        header.add(javax.swing.Box.createVerticalStrut(14));
        header.add(tabs);
        return header;
    }

    private JScrollPane buildCenter() {
        categoryHolder.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(categoryHolder);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.HEADER);
        bar.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        statusLabel.setFont(Theme.BODY_FONT);
        statusLabel.setForeground(Theme.CHOCOLATE);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    /**
     * Creates one page of cards per category from the repository's CURRENT products.
     * Runs on startup and whenever the whole product list is replaced.
     */
    private void rebuildProductCards() {
        categoryHolder.removeAll();
        cardsByProductId.clear();

        for (Category category : Category.values()) {
            List<Product> products = repository.getProductsByCategory(category);

            JPanel grid = new JPanel(new GridLayout(0, COLUMNS, 18, 18));
            grid.setOpaque(false);
            for (Product product : products) {
                ProductCard card = new ProductCard(product, this::handleAddRequested);
                cardsByProductId.put(product.getId(), card);
                grid.add(card);
            }
            if (products.isEmpty()) {
                JLabel empty = new JLabel("No products in this category yet.", SwingConstants.CENTER);
                empty.setFont(Theme.BODY_FONT);
                empty.setForeground(Theme.MUTED_TEXT);
                empty.setPreferredSize(new Dimension(300, 100));
                grid.add(empty);
            }

            // GridBagLayout centres the grid and keeps the cards at their normal size
            JPanel page = new JPanel(new GridBagLayout());
            page.setOpaque(false);
            page.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            page.add(grid);
            categoryHolder.add(page, category.name());
        }

        showCategory(selectedCategory);
        categoryHolder.revalidate();
        categoryHolder.repaint();
    }

    /** Called by a ProductCard after it validated the quantity. */
    private void handleAddRequested(Product product, int quantity) {
        if (selectionListener == null) {
            JOptionPane.showMessageDialog(this,
                    "The shopping cart is not connected yet.",
                    "Cake Haven", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            selectionListener.onProductSelected(product.getId(), quantity);
            statusLabel.setText("Added " + quantity + " x " + product.getName() + " to the cart.");
        } catch (RuntimeException ex) {
            // a problem inside the cart code must not crash the product screen
            JOptionPane.showMessageDialog(this,
                    "Could not add the item to the cart: " + ex.getMessage(),
                    "Cake Haven", JOptionPane.ERROR_MESSAGE);
        }
    }
}
