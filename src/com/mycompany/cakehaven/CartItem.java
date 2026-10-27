package com.mycompany.cakehaven;

/**
 * One line in the shopping cart: which product, its price, and how many the user chose.
 * Example: new CartItem("CAKE-01", "Chocolate Cake", 95.00, 2)
 *
 * The product ID is kept (not only the name) because the ORDER message to the
 * server must identify products by their ID (Phase 2).
 */
public class CartItem {

    private final String productId;
    private final String name;
    private final double price;
    private int quantity;

    public CartItem(String productId, String name, double price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // used when the user adds the same product again
    void addQuantity(int extra) {
        quantity += extra;
    }

    // used when the quantity is reduced to the stock
    void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }

    // price x quantity
    public double getSubtotal() {
        return price * quantity;
    }
}
