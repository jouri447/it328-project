package com.mycompany.cakehaven;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * The window that shows the cart (opened by the "Cart" button of the store window).
 * All the content is in CartPanel; this class only provides the window around it.
 */
public class CartFrame extends JFrame {

    private final CartPanel cartPanel;

    public CartFrame(Cart cart) {
        super("Cake Haven - Cart");
        cartPanel = new CartPanel(cart);
        add(cartPanel);

        // DISPOSE (not EXIT): closing the cart must NOT close the whole client application
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 420);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cartPanel.stopListening();
            }
        });
    }
}
