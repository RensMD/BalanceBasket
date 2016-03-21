package com.example.group21.balancebasket;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ProductsDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY = "CREATE TABLE " +  UserContract.NewProduct.TABLE_NAME + "(" + UserContract.NewProduct.COLUMN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + UserContract.NewProduct.PRODUCT_NAME + " TEXT, " +
            UserContract.NewProduct.PRODUCT_PRICE + " TEXT " + ");";

    public UserDBHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db){
        db.execSQL(CREATE_QUERY);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserContract.NewProduct.TABLE_NAME);
        onCreate(db);

    }

    public void addProduct (String name, String price, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserContract.NewProduct.PRODUCT_NAME, name);
        contentValues.put(UserContract.NewProduct.PRODUCT_PRICE, price);
        db.insert(UserContract.NewProduct.TABLE_NAME, null, contentValues);

    }

    public Cursor getProducts(SQLiteDatabase db){

        Cursor cursor;
        String[] projections = {UserContract.NewProduct.PRODUCT_NAME,UserContract.NewProduct.PRODUCT_PRICE};

        cursor= db.query(UserContract.NewProduct.TABLE_NAME, projections, null,null,null,null,null);
        return cursor;

    }




}
