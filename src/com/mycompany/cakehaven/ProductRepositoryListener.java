package com.mycompany.cakehaven;

/**
 * Lets other classes (such as the GUI) be told when the product data changes.
 * NOTE: these methods may be called from ANY thread (for example a network thread),
 * so a listener that touches Swing components must switch to the Event Dispatch Thread.
 * ProductPanel already does this for you.
 *
 * STATUS: fully implemented (interface).
 */
public interface ProductRepositoryListener {

    /** Called after the stock of one product changed. */
    void onStockChanged(Product product);

    /** Called after the whole product list was replaced (for example by server data). */
    void onProductsReloaded();
}
