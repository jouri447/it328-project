package com.mycompany.cakehaven;

/**
 * INTEGRATION POINT with the Shopping Cart component (Student 4).
 *
 * When the user clicks "Add" on a valid product/quantity, ProductPanel calls
 * onProductSelected(...). The cart developer implements this interface and
 * registers it with ProductPanel.setProductSelectionListener(...).
 *
 * Only the product ID and the quantity are sent, so this interface does not
 * depend on any cart class.
 *
 * STATUS: fully implemented (interface). The real implementation belongs to the cart.
 */
public interface ProductSelectionListener {

    /**
     * @param productId unique ID of the selected product (see Product.getId())
     * @param quantity  quantity chosen by the user, already validated (1 .. available stock)
     */
    void onProductSelected(String productId, int quantity);
}
