package com.gettipper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.gettipper.databinding.RegisterActivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //biewbinding
    private RegisterActivityBinding binding;

    //if code send failed, will use to resend code
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;

    private static final String TAG = "MAIN_TAG";

    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); //hide the title bar

        binding = RegisterActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerActivityLlPhoneRegister.setVisibility(View.VISIBLE); //show phone layout
        binding.registerActivityLlVerificationRegister.setVisibility(View.GONE); //hind code layout

        firebaseAuth = FirebaseAuth.getInstance();

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //this callback will be invoked in 2 situations:
                //1 -   Instant verification. In some cases the phone number can be instantly
                //      verified without needing to sent or enter a verification code
                //2 -   Auto-retrieval. On some verification Sms and perform verification without
                //      user action.

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                //this callback ins invoked in an invalid request for verification is made,
                //for instance if the phone number format is not valid

                pd.dismiss();
                Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                //The SMS verification code has been sent to the provider phone number, we
                //now need to ask the user to enter the code and then construct a credential
                //by combining the code with a verification ID

                Log.d(TAG, "onCodeSent: " + verificationId);

                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                //hide phone layout, shoe code layout
                binding.registerActivityLlPhoneRegister.setVisibility(View.GONE);
                binding.registerActivityLlVerificationRegister.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, "Verification code sent...", Toast.LENGTH_SHORT).show();
                binding.registerActivityTvCodeDescription.setText("Please Type the verification code we sent \nto " + binding.registerActivityEtPhoneNumber.getText().toString().trim());
            }
        };

        binding.registerActivityBtnPhoneContinue.setOnClickListener(view -> {
            String phone = binding.registerActivityEtPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(MainActivity.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
            } else {
                resendVerificationCode(phone, forceResendingToken);
            }
        });

        binding.registerActivityTvResendCode.setOnClickListener(view -> {
            String phone = binding.registerActivityEtPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(MainActivity.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
            } else {
                startPhoneNumberVerification(phone);
            }
        });

        //codeContinueBtn click: input verification code, validate, verify phone number with verification code
        binding.registerActivityBtnVerificationContinue.setOnClickListener(view -> {
            String code = binding.registerActivityEtCode.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(MainActivity.this, "Please enter verification code...", Toast.LENGTH_SHORT).show();
            } else {
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });
    }


    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifying phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone) //phone number, muse be with country code for example for Israel +972
                        .setTimeout(60L, TimeUnit.SECONDS) //The timeout and unit
                        .setActivity(this) //Activity (for callback binding)
                        .setCallbacks(mCallbacks) //OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending Code");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone) //phone number, muse be with country code for example for Israel +972
                        .setTimeout(60L, TimeUnit.SECONDS) //The timeout and unit
                        .setActivity(this) //Activity (for callback binding)
                        .setCallbacks(mCallbacks) //OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        pd.setMessage("Verifying Code");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Logging In");
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            pd.dismiss();
                            if (firebaseAuth.getCurrentUser()!=null) {
                                checkIfDBContainUser();

                            } else {
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void checkIfDBContainUser () {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userNameRef = rootRef.child("users").child(uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
                else {
                    User u = dataSnapshot.getValue(User.class);
                    if (u.getServiceProvider()) {
                        startActivity(new Intent(MainActivity.this, QR_generator.class));
                    }
                        else {
                        startActivity(new Intent(MainActivity.this, ScannerActivity.class));

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("ttt", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}

