package com.mycompany.cakehaven;

/**
 * One product in the Cake Haven store (data only, no GUI code).
 * Everything is fixed after creation except the available stock,
 * which changes when an order happens (the server will tell us the new value).
 *
 * STATUS: fully implemented.
 */
public class Product {

    /** Currency label used when showing prices. Change it here in one place. */
    public static final String CURRENCY = "SAR";

    private final String id;
    private final String name;
    private final Category category;
    private final double price;
    private final String imagePath;
    // volatile: the stock may be changed by a network thread and read by the GUI thread
    private volatile int availableStock;

    /**
     * @param id             unique product ID (for example "CAKE-01")
     * @param name           product name shown to the user
     * @param category       the category this product belongs to
     * @param price          price of ONE item (must not be negative)
     * @param availableStock how many items are in stock (must not be negative)
     * @param imagePath      classpath path (e.g. "/com/mycompany/cakehaven/images/x.jpg") or file path;
     *                       if the image cannot be loaded a placeholder is shown instead
     */
    public Product(String id, String name, Category category,
                   double price, int availableStock, String imagePath) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product id must not be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name must not be empty");
        }
        if (category == null) {
            throw new IllegalArgumentException("Product category must not be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        if (availableStock < 0) {
            throw new IllegalArgumentException("Stock must not be negative");
        }
        this.id = id.trim();
        this.name = name.trim();
        this.category = category;
        this.price = price;
        this.availableStock = availableStock;
        this.imagePath = imagePath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    /** Sets the stock. Negative values are rejected. */
    public void setAvailableStock(int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock must not be negative");
        }
        this.availableStock = newStock;
    }

    public boolean isOutOfStock() {
        return availableStock <= 0;
    }

    /** Price as text, for example "SAR 95.00". */
    public String getFormattedPrice() {
        return String.format("%s %.2f", CURRENCY, price);
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}
