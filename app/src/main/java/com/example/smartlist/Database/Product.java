package com.example.smartlist.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "description")
    @NonNull
    public String description;

    @ColumnInfo(name = "packetSize")
    @NonNull
    public double packetSize;

    @ColumnInfo(name = "packetSizeType")
    @NonNull
    public String packetSizeType;

    @ColumnInfo(name = "packetPrice")
    @NonNull
    public double packetPrice;

    @ColumnInfo(name = "unitPrice")
    @NonNull
    public double unitPrice;

    @ColumnInfo(name = "unitType")
    @NonNull
    public String unitType;

    @ColumnInfo(name = "productCategory")
    @NonNull
    public String productCategory;

    @ColumnInfo(name = "store")
    @NonNull
    public String store;

    @ColumnInfo(name = "hits")
    @NonNull
    public int hits;

    @Override
    public String toString() {
        return description;
    }
}
