package com.gettipper;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MyFirebaseDB {

    public interface CallBack_String {
        void dataReady(String value);
    }

    public static void getPaypalAccountByUid(String Uid, CallBack_String callBack_string) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(Uid).child("paypalName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue().toString();
                if (callBack_string != null) {
                    callBack_string.dataReady(value);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





//    public static void checkIfDBContainUser(String uid, CallBack_Boolean callBackBoolean) {
////    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference userNameRef = rootRef.child("users").child(uid);
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    callBackBoolean.dataReady(false);
//                    //startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//                } else {
//                    User u = dataSnapshot.getValue(User.class);
//                    callBackBoolean.getUser(u);
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("ttt", databaseError.getMessage()); //Don't ignore errors!
//            }
//        };
//        userNameRef.addListenerForSingleValueEvent(eventListener);
//
//    }
}






























