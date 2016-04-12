package com.example.group21.balancebasket;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.example.group21.balancebasket.UserContract.NewProduct.*;

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ProductsDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY = "CREATE TABLE " +  TABLE_NAME + "(" + COLUMN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + PRODUCT_NAME + " TEXT, " +
            PRODUCT_PRICE + " TEXT " + ");";

    public UserDBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db){
        db.execSQL(CREATE_QUERY);
        initProductList(db);
    }

    // fill database with initial products
    private void initProductList(SQLiteDatabase db) {
        // remove products from the database first
        db.execSQL("DELETE FROM " + TABLE_NAME);

        // insert products into the database
        this.addProduct(db, "apple", 0.25);
        this.addProduct(db, "banana", 0.15);
        this.addProduct(db, "toothpaste", 1.25);
        this.addProduct(db, "cookies", 3.75);
        this.addProduct(db, "bullshit", 2.50);
    }

    // insert product into the database
    public void addProduct (SQLiteDatabase db, String name, double price){

        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_NAME, name);
        contentValues.put(PRODUCT_PRICE, price);
        db.insert(TABLE_NAME, null, contentValues);
    }

    // delete product from the database
    public boolean removeProduct (String name){
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = new String[] {name};
        int result = db.delete(TABLE_NAME, PRODUCT_NAME + "= ? ", whereArgs);
        // if product is deleted result > 0 and this method returns true
        return result > 0;
        }

    public String calculateTotalPrice(SQLiteDatabase db){
        Cursor c = db.rawQuery("SELECT SUM(PRODUCT_PRICE) AS TOTAL FROM " + TABLE_NAME + ";", null);
        String total = "";
        int iPrice = c.getColumnIndex(PRODUCT_PRICE);

        for(c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
            total = total + c.getString(iPrice);
        }

        return total;
    }


    // get all products from the database
    public static List<Product> getProducts(SQLiteDatabase db){
        Cursor cursor = db.query(TABLE_NAME, new String[] { COLUMN_ID, PRODUCT_NAME, PRODUCT_PRICE },
                null, null, null, null, null);
        List<Product> products = new ArrayList<Product>();
        while (cursor.moveToNext()) {
            // get column indices + values of those columns
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(PRODUCT_NAME));
            String price = cursor.getString(cursor.getColumnIndex(PRODUCT_PRICE));
            Product product = new Product(name, price);
            products.add(product);
            Log.i("LOG_TAG", "ROW " + id + " HAS NAME " + name + " HAS PRICE " + price);
        }
        cursor.close();

        return products;
  }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

}
