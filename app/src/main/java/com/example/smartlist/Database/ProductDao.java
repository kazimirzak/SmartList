package com.example.smartlist.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ProductDao {

    @Insert()
    void insertProduct(Product product);

    @Query("SELECT * " +
            "FROM products " +
            "WHERE hits > 0 " +
            "ORDER BY hits DESC")
    List<Product> getProductsWithHits();

    @Query("SELECT * " +
            "FROM products " +
            "WHERE description = :input AND unitPrice= (SELECT MIN(unitPrice) FROM products WHERE description = :input)")
    Product getCheapestProductByDescription(String input);

    @RawQuery
    List<Product> getProductsByRawQuery(SupportSQLiteQuery query);

    @Query("SELECT * " +
            "FROM products")
    List<Product> getAll();

    @Update(onConflict = REPLACE)
    void updateProduct(Product product);

    @Query("DELETE FROM products")
    void nukeTable();

    @Query("SELECT * " +
            "FROM products " +
            "WHERE description = :input " +
            "ORDER BY unitPrice ASC")
    List<Product> getCheapestProductsByDescription(String input);
}
