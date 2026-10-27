package com.mycompany.cakehaven;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Holds all products in memory and is the ONLY place where product data is changed.
 * It has no Swing code, so the server-side/network code can call it safely
 * from any thread.
 *
 * Methods marked [IMPLEMENTED] are complete.
 * Methods marked [SERVER HOOK] are complete too, but are meant to be called
 * by the future client-server code when data arrives from the server.
 */
public class ProductRepository {

    // LinkedHashMap keeps the products in the order they were added
    private final Map<String, Product> productsById = new LinkedHashMap<>();
    private final List<ProductRepositoryListener> listeners = new CopyOnWriteArrayList<>();

    /** Creates an empty repository. Call loadSampleData() or replaceAllProducts(...) to fill it. */
    public ProductRepository() {
    }

    // ------------------------------------------------------------------
    // Loading data
    // ------------------------------------------------------------------

    /** [IMPLEMENTED - TEMPORARY SOURCE] Fills the repository with the local sample data. */
    public void loadSampleData() {
        replaceAllProducts(SampleProductData.createSampleProducts());
    }

    /**
     * [SERVER HOOK] Replaces the complete product list (e.g. the catalogue received from the server).
     * Products with a duplicate ID are ignored (the first one wins).
     * Listeners are told so the screen is rebuilt.
     */
    public void replaceAllProducts(List<Product> newProducts) {
        synchronized (this) {
            productsById.clear();
            if (newProducts != null) {
                for (Product product : newProducts) {
                    if (product != null && !productsById.containsKey(product.getId())) {
                        productsById.put(product.getId(), product);
                    }
                }
            }
        }
        for (ProductRepositoryListener listener : listeners) {
            listener.onProductsReloaded();
        }
    }

    // ------------------------------------------------------------------
    // Reading data
    // ------------------------------------------------------------------

    /** [IMPLEMENTED] Returns a copy of all products (safe to loop over). */
    public synchronized List<Product> getAllProducts() {
        return new ArrayList<>(productsById.values());
    }

    /** [IMPLEMENTED] Returns only the products of the given category. */
    public synchronized List<Product> getProductsByCategory(Category category) {
        List<Product> result = new ArrayList<>();
        for (Product product : productsById.values()) {
            if (product.getCategory() == category) {
                result.add(product);
            }
        }
        return result;
    }

    /** [IMPLEMENTED] Finds a product by its ID, or returns null if there is none. */
    public synchronized Product findById(String productId) {
        if (productId == null) {
            return null;
        }
        return productsById.get(productId.trim());
    }

    // ------------------------------------------------------------------
    // Stock updates
    // ------------------------------------------------------------------

    /**
     * [SERVER HOOK] Sets the available stock of one product, for example when a stock-update
     * message arrives from the server. Listeners (the GUI) are notified.
     *
     * @return true if the stock was updated; false if the product ID is unknown
     *         or the new stock is negative (nothing is changed in that case)
     */
    public boolean updateStock(String productId, int newStock) {
        if (newStock < 0) {
            return false;
        }
        Product product = findById(productId);
        if (product == null) {
            return false;
        }
        product.setAvailableStock(newStock);
        for (ProductRepositoryListener listener : listeners) {
            listener.onStockChanged(product);
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Listeners
    // ------------------------------------------------------------------

    /** [IMPLEMENTED] Registers a listener that is told about product/stock changes. */
    public void addListener(ProductRepositoryListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /** [IMPLEMENTED] Removes a previously registered listener. */
    public void removeListener(ProductRepositoryListener listener) {
        listeners.remove(listener);
    }
}
