package com.example.group21.balancebasket;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DataListActivity extends ListFragment {

    ListView listView;
    SQLiteDatabase sqLiteDatabase;
    UserDBHelper userDbHelper;
    Cursor cursor;
    ListDataAdapter listDataAdapter;


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        listDataAdapter = new ListDataAdapter(getActivity().getApplicationContext(), R.layout.row_layout);
//        listView.setAdapter(listDataAdapter);
//
//        userDbHelper = new UserDBHelper(getActivity().getApplicationContext());
//        sqLiteDatabase = userDbHelper.getReadableDatabase();
//        cursor = userDbHelper.getProducts(sqLiteDatabase);
//
//        if (cursor.moveToFirst()) {
//            do {
//
//                String name, price;
//                name = cursor.getString(0);
//                price = cursor.getString(1);
//                DataProvider dataProvider = new DataProvider(name, price);
//                listDataAdapter.add(dataProvider);
//
//
//            } while (cursor.moveToNext());
//        }
//
//    }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saverInstaceState) {
    View rootView = inflater.inflate(R.layout.data_list_layout, container, false);

    listDataAdapter = new ListDataAdapter(getActivity().getApplicationContext(), R.layout.row_layout);
    listView.setAdapter(listDataAdapter);

    userDbHelper = new UserDBHelper(getActivity().getApplicationContext());
    sqLiteDatabase = userDbHelper.getReadableDatabase();
    cursor = userDbHelper.getProducts(sqLiteDatabase);

    if (cursor.moveToFirst()) {
        do {

            String name, price;
            name = cursor.getString(0);
            price = cursor.getString(1);
            DataProvider dataProvider = new DataProvider(name, price);
            listDataAdapter.add(dataProvider);


        } while (cursor.moveToNext());
    }

    return rootView;

    }

}

