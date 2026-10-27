package com.mycompany.cakehaven;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * The main client window after Connect: it joins the pieces of the team together.
 *   - ProductPanel (Student 3)   : categories, product cards, Add buttons
 *   - Cart / CartFrame (Student 4): the shopping cart
 *   - ServerConnection (Student 2): the connection that was opened by ConnectFrame
 *
 * STATUS: integration glue (normally Student 5's job). It is small on purpose,
 * so it can be replaced or extended by the group.
 * Phase 1: the product list is the local sample data (SampleProductData). In Phase 2 the server keeps the stock.
 */
public class StoreFrame extends JFrame {

    private final ServerConnection connection;
    private final ProductRepository repository = new ProductRepository();
    private final Cart cart;
    private CartFrame cartFrame;   // the cart window (only one at a time)

    public StoreFrame(ServerConnection connection) {
        this.connection = connection;

        setTitle("Cake Haven - Store (" + connection.getUsername() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // data (TEMPORARY sample data) -> cart -> product screen
        repository.loadSampleData();
        cart = new Cart(repository);
        ProductPanel productPanel = new ProductPanel(repository);
        productPanel.setProductSelectionListener(cart);   // "Add" clicks go to the cart

        // a thin bar with the Cart button above the product screen
        JButton cartButton = new JButton("Cart");
        cartButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        cartButton.setBackground(new Color(0xE0708E));
        cartButton.setForeground(Color.WHITE);
        cartButton.setOpaque(true);
        cartButton.setBorderPainted(false);
        cartButton.setFocusPainted(false);
        cartButton.setPreferredSize(new Dimension(110, 34));
        cartButton.addActionListener(e -> openCart());

        // My Orders button (Functions 6-7 - the server part comes in Phase 2)
        JButton ordersButton = new JButton("My Orders");
        ordersButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        ordersButton.addActionListener(e -> openOrders());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        topBar.setBackground(new Color(0xFBE4EA));
        topBar.add(ordersButton);
        topBar.add(cartButton);

        add(topBar, BorderLayout.NORTH);
        add(productPanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(700, 600));
        setSize(1100, 760);
        setLocationRelativeTo(null);

        // when the user closes the store window, close the connection too
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                StoreFrame.this.connection.close();
            }
        });
    }

    private void openCart() {
        if (cartFrame == null || !cartFrame.isDisplayable()) {
            cartFrame = new CartFrame(cart);
            cartFrame.setVisible(true);
        } else {
            cartFrame.toFront();
        }
    }

    // the orders window: the user's orders (by timestamp) and a Cancel Order button
    private void openOrders() {
        JFrame ordersFrame = new JFrame("Cake Haven - My Orders");
        JList<String> ordersList = new JList<>(new DefaultListModel<String>()); // filled in Phase 2
        JButton cancelButton = new JButton("Cancel Order");
        cancelButton.addActionListener(e -> {
            if (ordersList.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(ordersFrame, "Please select an order to cancel.");
            } else {
                JOptionPane.showMessageDialog(ordersFrame, "Cancelling orders will be available in Phase 2.");
            }
        });

        ordersFrame.add(new JLabel("  Your orders:"), BorderLayout.NORTH);
        ordersFrame.add(new JScrollPane(ordersList), BorderLayout.CENTER);
        ordersFrame.add(cancelButton, BorderLayout.SOUTH);
        ordersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // closing it keeps the store open
        ordersFrame.setSize(400, 300);
        ordersFrame.setLocationRelativeTo(this);
        ordersFrame.setVisible(true);
    }
}
