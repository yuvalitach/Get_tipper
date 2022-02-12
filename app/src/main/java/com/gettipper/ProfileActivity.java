package com.gettipper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private TextInputLayout EDT_name, EDT_email, EDT_paypalName;
    private MaterialButton btn_continue;
    private MaterialRadioButton RB_customer, RB_serviceProvider;
    private Intent scannerActivity, QRActivity;
    private String  uid;
    Validator v1, v2, v3;
    private Button btn_logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); //hide the action bar

        setContentView(R.layout.profile_activity);
        init();
        findViewById();
        addValidators();
        btn_continue.setOnClickListener(view -> submit());
        btn_logOut.setOnClickListener(view -> finishAffinity());
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }



    private void submit() {
        if ((v1.validateIt())&&(v2.validateIt())&&v1.getError().equals("")&&v2.getError().equals("")&&v3.getError().equals(""))
        {
            User user = new User().setFullName(EDT_name.getEditText().getText().toString())
                    .setMail(EDT_email.getEditText().getText().toString())
                    .setServiceProvider(RB_serviceProvider.isChecked())
                    .setPaypalName(EDT_paypalName.getEditText().getText().toString());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users");
                    myRef.child(uid).setValue(user);

            if (RB_customer.isChecked()) {
                startActivity(scannerActivity);
            }
            if (RB_serviceProvider.isChecked()) {
                startActivity(QRActivity);
            }

        }
        else {
            Toast errorToast = Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT);
            errorToast.show();
        }

    }

    private void addValidators() {
        v1 = Validator.Builder
                .make(EDT_email)
                .addWatcher(new Validator.Watcher_Email("Not valid email"))
                .addWatcher(new Validator.Watcher_Not_Empty("Please type mail"))
                .build();

        v2 = Validator.Builder
                .make(EDT_name)
                .addWatcher(new Validator.Watcher_Full_Name("Not valid name"))
                .addWatcher(new Validator.Watcher_Not_Empty("Please type name"))
                .build();

        v3 = Validator.Builder
                .make(EDT_paypalName)
                .addWatcher(new Validator.Watcher_Not_Space("Not valid account"))
                .addWatcher(new Validator.Watcher_Not_Empty("Please type your paypal name account"))
                .build();
    }


    private void findViewById() {
        EDT_name = findViewById(R.id.profile_activity_EDT_name);
        EDT_email = findViewById(R.id.profile_activity_EDT_email);
        EDT_paypalName = findViewById(R.id.profile_activity_EDT_paypalName);
        btn_continue = findViewById(R.id.profile_activity_btn_phone_continue);
        RB_customer = findViewById(R.id.profile_activity_RB_customer);
        RB_serviceProvider = findViewById(R.id.profile_activity_RB_serviceProvider);
        btn_logOut = findViewById(R.id.profile_activity_btn_logout);
    }

    private void init() {
        scannerActivity = new Intent(this, ScannerActivity.class);
        QRActivity = new Intent(this, QR_generator.class);

    }



//    private ProfileActivityBinding binding;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ProfileActivityBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        checkUserStatus();
//
//
//    }
//
//    private void checkUserStatus() {
//    //get current user
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        if(firebaseUser!=null){
//            //user is logged in
//            String phone = firebaseUser.getPhoneNumber();
//
////            binding.ProfileActivityPhoneTv.setText(phone);
//        }
//
//        else{
//            //use is not logged in
//            finish();
//        }
//
//
//    }
}