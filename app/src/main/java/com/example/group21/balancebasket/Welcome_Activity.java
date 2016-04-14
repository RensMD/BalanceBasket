package com.example.group21.balancebasket;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Welcome_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);
    }
    public void Start_Connect_Activity(View view) {
        Intent intent = new Intent(this, BasketDrawer.class);
        startActivity(intent);
    }

    public void showTerms() {
        DialogFragment newFragment = new TermsDialogFragment();
        newFragment.show(getFragmentManager(), "Terms");
    }

    public void show_Terms(View view) {
        showTerms();
    }
}
