package com.example.smartlist.Database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Repository {

    private static final String DB_NAME = "db";

    private static LocalDatabase database;

    public static void init(Context context) {
        database = Room.databaseBuilder(context, LocalDatabase.class, DB_NAME).build();
    }

    /**
     * Inserts a product into the rooms database.
     */

    public static void insertProduct(Product product) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.productDao().insertProduct(product);
                return null;
            }
        }.execute();
    }

    /**
     * Gets all products with hits, sorted in descending order.
     */

    public static List<Product> getProductsWithHits() {
        List<Product> result = new ArrayList<>();
        try {
            result = new AsyncTask<Void, Void, List<Product>>() {
                @Override
                protected List<Product> doInBackground(Void... voids) {
                    return database.productDao().getProductsWithHits();
                }
            }.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Runs a given query on the database.
     */

    public static List<Product> getProductsByRawQuery(SupportSQLiteQuery query) {
        List<Product> result = new ArrayList<>();
        try {
            result = new AsyncTask<Void, Void, List<Product>>() {
                @Override
                protected List<Product> doInBackground(Void... voids) {
                    return database.productDao().getProductsByRawQuery(query);
                }
            }.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Querys the database for products with the descriptions. Sorts by price.
     * @param description
     * @return
     */

    public static Product getCheapestProductByDescription(String description) {
        Product product = null;
        try {
            product = new AsyncTask<Void, Void, Product>() {
                @Override
                protected Product doInBackground(Void... voids) {
                    return database.productDao().getCheapestProductByDescription(description);
                }
            }.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return product;
    }

    /**
     * Updates the given product in the database.
     * @param product
     */

    public static void updateProduct(Product product) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.productDao().updateProduct(product);
                return null;
            }
        }.execute();
    }

    /**
     * Gets multiple porudcts by the description given. Sorts by price.
     */

    public static List<Product> getCheapestProductsByDescription(String description) {
        List<Product> result = new ArrayList<>();
        try {
            result = new AsyncTask<Void, Void, List<Product>>() {
                @Override
                protected List<Product> doInBackground(Void... voids) {
                    return database.productDao().getCheapestProductsByDescription(description);
                }
            }.execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
