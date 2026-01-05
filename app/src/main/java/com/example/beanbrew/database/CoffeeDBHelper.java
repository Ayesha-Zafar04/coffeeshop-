package com.example.beanbrew.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.beanbrew.models.Coffee;
import java.util.ArrayList;
import java.util.List;

public class CoffeeDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BeanBrew.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_COFFEE = "coffee";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DESCRIPTION = "description";

    public CoffeeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COFFEE_TABLE = "CREATE TABLE " + TABLE_COFFEE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_COFFEE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COFFEE);
        onCreate(db);
    }

    public long insertCoffee(String name, double price, String category, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_DESCRIPTION, description);
        long id = db.insert(TABLE_COFFEE, null, values);
        db.close();
        return id;
    }

    public boolean updateCoffee(int id, String name, double price, String category, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_DESCRIPTION, description);
        int rowsAffected = db.update(TABLE_COFFEE, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean deleteCoffee(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_COFFEE, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public List<Coffee> getAllCoffee() {
        List<Coffee> coffeeList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_COFFEE + " ORDER BY " + COLUMN_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Coffee coffee = new Coffee();
                coffee.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                coffee.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                coffee.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                coffee.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                coffee.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                coffeeList.add(coffee);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return coffeeList;
    }

    public Coffee getCoffeeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COFFEE, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            Coffee coffee = new Coffee();
            coffee.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            coffee.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            coffee.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
            coffee.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
            coffee.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            cursor.close();
            db.close();
            return coffee;
        }
        if (cursor != null) cursor.close();
        db.close();
        return null;
    }
}