package com.mycompany.cakehaven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The shopping cart (the "model"). It only stores the chosen products and calculates the total.
 * It has no GUI code and no socket code.
 *
 * It implements ProductSelectionListener, which is the integration point that
 * Student 3 prepared: ProductPanel calls onProductSelected(productId, quantity)
 * every time the user clicks "Add" with a valid quantity.
 *
 * One Cart object is created per client (see StoreFrame).
 * It must be used from the Swing (Event Dispatch) thread.
 */
public class Cart implements ProductSelectionListener {

    /** Anyone who wants to be told "the cart changed" (e.g. CartPanel) implements this. */
    public interface CartListener {
        void cartChanged();
    }

    private final ProductRepository repository;
    private final List<CartItem> items = new ArrayList<>();
    private final List<CartListener> listeners = new ArrayList<>();

    public Cart(ProductRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("repository must not be null");
        }
        this.repository = repository;
    }

    /**
     * Called by ProductPanel when the user clicks Add.
     * We only receive the ID and the quantity, so the name and price are looked up
     * in the ProductRepository (Student 3's class).
     * An exception here is shown to the user by ProductPanel as an error message.
     */
    @Override
    public void onProductSelected(String productId, int quantity) {
        Product product = repository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Unknown product: " + productId);
        }
        addProduct(product, quantity);
    }

    /**
     * Adds a product to the cart.
     * If the same product is already in the cart, its quantity increases (no second line).
     */
    public void addProduct(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("product must not be null");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        CartItem existing = find(product.getId());
        // the cart can't hold more than the stock (counting what is already in the cart)
        int inCart = 0;
        if (existing != null) {
            inCart = existing.getQuantity();
        }
        if (inCart + quantity > product.getAvailableStock()) {
            throw new IllegalArgumentException("you already have " + inCart + " in the cart and only "
                    + product.getAvailableStock() + " are in stock.");
        }
        if (existing != null) {
            existing.addQuantity(quantity);
        } else {
            items.add(new CartItem(product.getId(), product.getName(), product.getPrice(), quantity));
        }
        fireCartChanged();
    }

    /**
     * Removes the whole line of this product from the cart (all its quantity).
     * @return true if the product was in the cart and was removed
     */
    public boolean removeItem(String productId) {
        CartItem existing = find(productId);
        if (existing == null) {
            return false;
        }
        items.remove(existing);
        fireCartChanged();
        return true;
    }

    /** Read-only view of the items (outside code cannot add or remove directly). */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /** Sum of (price x quantity) for all items. */
    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void addListener(CartListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CartListener listener) {
        listeners.remove(listener);
    }

    private CartItem find(String productId) {
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    private void fireCartChanged() {
        // copy the list, in case a listener removes itself while being notified
        for (CartListener listener : new ArrayList<>(listeners)) {
            listener.cartChanged();
        }
    }
}
