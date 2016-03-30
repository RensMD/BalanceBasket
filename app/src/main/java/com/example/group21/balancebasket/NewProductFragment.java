package com.example.group21.balancebasket;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class NewProductFragment extends Fragment implements View.OnClickListener {
    EditText productName, productPrice;
    Context context;
    UserDBHelper userDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Button AddButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saverInstaceState) {
        View view = inflater.inflate(R.layout.new_product_layout, container, false);
        Button AddProduct = (Button) view.findViewById(R.id.AddButton);
        AddProduct.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        productName = (EditText) getView().findViewById(R.id.ProductName);
        productPrice = (EditText) getView().findViewById(R.id.ProductPrice);
        AddButton = (Button) getView().findViewById(R.id.AddButton);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.AddButton:
                String name = productName.getText().toString();
                String price = productPrice.getText().toString();
                userDbHelper = new UserDBHelper(context);
                sqLiteDatabase = userDbHelper.getWritableDatabase();
                userDbHelper.addProduct(name, price, sqLiteDatabase);
                Toast.makeText(getActivity().getBaseContext(), "Product Added", Toast.LENGTH_LONG).show();
                productName.setText("");
                productPrice.setText("");


        }
    }
}
