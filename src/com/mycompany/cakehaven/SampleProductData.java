package com.mycompany.cakehaven;

import java.util.ArrayList;
import java.util.List;

/**
 * ALL sample data lives in this one class, so it is easy to change names,
 * prices, stock quantities or image files.
 *
 * STATUS: TEMPORARY PLACEHOLDER DATA. When the server sends the real product list,
 * pass that list to ProductRepository.replaceAllProducts(...) instead.
 */
public final class SampleProductData {

    /** Folder (inside the source package) that contains the product images. */
    private static final String IMAGE_FOLDER = "/com/mycompany/cakehaven/images/";

    private SampleProductData() {
        // utility class - no objects needed
    }

    /** Creates the 12 sample products (4 per category). */
    public static List<Product> createSampleProducts() {
        List<Product> list = new ArrayList<>();

        // ---- Cakes ----
        list.add(new Product("CAKE-01", "Chocolate Cake",   Category.CAKES, 95.00, 8, IMAGE_FOLDER + "chocolate_cake.jpg"));
        list.add(new Product("CAKE-02", "Vanilla Cake",     Category.CAKES, 85.00, 6, IMAGE_FOLDER + "vanilla_cake.jpg"));
        list.add(new Product("CAKE-03", "Red Velvet Cake",  Category.CAKES, 110.00, 3, IMAGE_FOLDER + "red_velvet_cake.jpg"));
        list.add(new Product("CAKE-04", "Strawberry Cake",  Category.CAKES, 100.00, 5, IMAGE_FOLDER + "strawberry_cake.jpg"));

        // ---- Cupcakes ----
        list.add(new Product("CUP-01", "Chocolate Cupcake",   Category.CUPCAKES, 12.00, 24, IMAGE_FOLDER + "chocolate_cupcake.jpg"));
        list.add(new Product("CUP-02", "Vanilla Cupcake",     Category.CUPCAKES, 10.00, 30, IMAGE_FOLDER + "vanilla_cupcake.jpg"));
        list.add(new Product("CUP-03", "Red Velvet Cupcake",  Category.CUPCAKES, 14.00, 18, IMAGE_FOLDER + "red_velvet_cupcake.jpg"));
        list.add(new Product("CUP-04", "Oreo Cupcake",        Category.CUPCAKES, 14.00, 20, IMAGE_FOLDER + "oreo_cupcake.jpg"));

        // ---- Pastries ----
        list.add(new Product("PAS-01", "Croissant",       Category.PASTRIES, 8.00, 25, IMAGE_FOLDER + "croissant.jpg"));
        list.add(new Product("PAS-02", "Cheese Danish",   Category.PASTRIES, 9.00, 15, IMAGE_FOLDER + "cheese_danish.jpg"));
        list.add(new Product("PAS-03", "Apple Turnover",  Category.PASTRIES, 10.00, 12, IMAGE_FOLDER + "apple_turnover.jpg"));
        list.add(new Product("PAS-04", "Cinnamon Roll",   Category.PASTRIES, 11.00, 16, IMAGE_FOLDER + "cinnamon_roll.jpg"));

        return list;
    }
}
