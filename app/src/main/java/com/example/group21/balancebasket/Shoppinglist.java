package com.example.group21.balancebasket;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Shoppinglist extends AppCompatActivity {

    EditText SLInput;
    TextView SList;
    MyDBHandler dbHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SLInput = (EditText) findViewById(R.id.SLInput);
        SList = (TextView) findViewById(R.id.SList);
        dbHandler = new MyDBHandler(this, null, null, 1);
        printDatabase ();

    }

    public void printDatabase(){
        String dbString = dbHandler.databaseToString();
        SList.setText(dbString);
        SLInput.setText("");
    }

    public void RemoveButtonClicked (View view) {
        String inputText = SLInput.getText().toString();
        dbHandler.removeProduct(inputText);
        printDatabase();
    }

    public void AddButtonClicked(View view) {
        Products products = new Products(SLInput.getText().toString());
        dbHandler.addProduct(products);
        printDatabase();

    }
}
