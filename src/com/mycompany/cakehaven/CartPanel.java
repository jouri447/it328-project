package com.mycompany.cakehaven;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * The cart screen (the "view"): a table with Product / Price / Quantity / Subtotal
 * for every item in the Cart, and the total at the bottom.
 * It listens to the Cart, so it refreshes itself whenever the cart changes.
 * It is a JPanel, so it can be placed inside any window (CartFrame uses it).
 */
public class CartPanel extends JPanel implements Cart.CartListener {

    // same colours as Student 3's Theme (her Theme class is not public, so they are repeated here)
    private static final Color BACKGROUND = new Color(0xFFF8F1);
    private static final Color HEADER = new Color(0xFBE4EA);
    private static final Color CHOCOLATE = new Color(0x5A3A2E);
    private static final Color GRID = new Color(0xEBD9CF);
    private static final Color PINK = new Color(0xE0708E);

    private static final String[] COLUMNS = {"Product", "Price", "Quantity", "Subtotal"};

    private final Cart cart;
    private final DefaultTableModel tableModel;
    private final JLabel totalLabel = new JLabel();
    private final JButton removeButton = new JButton("Remove selected");
    private final JTable table;

    public CartPanel(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("cart must not be null");
        }
        this.cart = cart;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel("Your Cart", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLabel.setForeground(CHOCOLATE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(HEADER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // the user can only read the table
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(CHOCOLATE);
        table.setGridColor(GRID);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        // numbers (Price, Quantity, Subtotal) are centred
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        // bottom bar: "Remove selected" button on the left, the total on the right
        removeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        removeButton.setBackground(PINK);
        removeButton.setForeground(Color.WHITE);
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.setFocusPainted(false);
        removeButton.setEnabled(false);   // enabled only when a row is selected
        removeButton.addActionListener(e -> removeSelectedItem());
        table.getSelectionModel().addListSelectionListener(
                e -> removeButton.setEnabled(table.getSelectedRow() >= 0));

        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        totalLabel.setForeground(CHOCOLATE);

        // Order button (Function 4 - the server part comes in Phase 2)
        JButton orderButton = new JButton("Order");
        orderButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        orderButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Ordering will be available in Phase 2.", "Cake Haven", JOptionPane.INFORMATION_MESSAGE));

        JPanel rightSide = new JPanel();
        rightSide.setOpaque(false);
        rightSide.add(totalLabel);
        rightSide.add(orderButton);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 26));
        bottomBar.add(removeButton, BorderLayout.WEST);
        bottomBar.add(rightSide, BorderLayout.EAST);
        add(bottomBar, BorderLayout.SOUTH);

        cart.addListener(this);   // subscribe: tell me when the cart changes
        refresh();                // show what is already in the cart
    }

    /** Call when the window is closed, so the cart stops notifying this (dead) panel. */
    public void stopListening() {
        cart.removeListener(this);
    }

    // called by the Cart every time something changes
    @Override
    public void cartChanged() {
        // Swing components may only be touched on the Swing thread
        if (SwingUtilities.isEventDispatchThread()) {
            refresh();
        } else {
            SwingUtilities.invokeLater(this::refresh);
        }
    }

    // removes the product of the selected table row from the cart
    private void removeSelectedItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        // table rows are in the same order as cart.getItems()
        CartItem selected = cart.getItems().get(row);
        cart.removeItem(selected.getProductId());   // the cart then calls cartChanged() -> refresh()
    }

    // rebuild the table rows and the total from the Cart data
    private void refresh() {
        tableModel.setRowCount(0);
        for (CartItem item : cart.getItems()) {
            tableModel.addRow(new Object[]{
                item.getName(),
                money(item.getPrice()),
                item.getQuantity(),
                money(item.getSubtotal())
            });
        }
        totalLabel.setText("Total: " + money(cart.getTotal()));
    }

    // "SAR 95.00". Locale.US keeps the digits 0-9 on any computer.
    private static String money(double value) {
        return Product.CURRENCY + " " + String.format(Locale.US, "%.2f", value);
    }
}
