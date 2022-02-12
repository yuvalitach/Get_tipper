package com.gettipper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;

public class CompleteActivity extends AppCompatActivity {

    private MaterialButton btn_back, btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        btn_back = findViewById(R.id.complete_activity_btn_Back);
        btn_exit = findViewById(R.id.complete_activity_btn_Exit);

        btn_back.setOnClickListener(view -> startActivity(new Intent(this,ProfileActivity.class)));
        btn_exit.setOnClickListener(view -> finishAffinity());

    }
}