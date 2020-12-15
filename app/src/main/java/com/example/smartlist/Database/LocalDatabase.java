package com.example.smartlist.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class}, version = 1, exportSchema = false)

public abstract class LocalDatabase extends RoomDatabase {

    public abstract ProductDao productDao();
}
