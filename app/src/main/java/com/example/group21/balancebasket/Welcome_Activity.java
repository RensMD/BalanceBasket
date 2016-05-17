package com.example.group21.balancebasket;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Welcome_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);
        findViewById(R.id.agree_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome_Activity.this, BasketDrawer.class));
            }
        });
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
