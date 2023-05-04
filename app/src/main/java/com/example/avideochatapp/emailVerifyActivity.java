package com.example.avideochatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class emailVerifyActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);
        //getActionBar().setTitle("Verify Email");
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified())
        {
            sendUserToActivity(user);
        }
        else sendEmail();
    }

    private void sendEmail() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button
                        //findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(emailVerifyActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("TAG", "sendEmailVerification", task.getException());
                            Toast.makeText(emailVerifyActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sendEmail(View view) {
        sendEmail();
    }

    public void onProceedClick(View view) {
        mAuth.getCurrentUser().reload();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified())
        {
            sendUserToActivity(user);
        }
        else {
            Toast.makeText(getApplicationContext(),"Email is not verified. Try again",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendUserToActivity(FirebaseUser user) {
        ///<summary>
        /// Is user has already created a profile, then send him to Chat Activity, else send him to
        ///SetupProfile Activity
        ///@param user: gives userID from FirebaseAuth, and if the user has created a profile
        ///then userID from auth will exist in Firebase Database, else, we have to set up profile
        ///and store data
        ///</summary>
        String userID = user.getUid();
        Toast.makeText(getApplicationContext(),"Sign In Success!",Toast.LENGTH_SHORT).show();
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("userId").equalTo(userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() > 0) {
                    Log.d("TAG", "User already exists");
                    Intent intent = new Intent(emailVerifyActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    // 1 or more users exist which have the username property "usernameToCheckIfExists"
                } else {
                    Log.d("TAG", "User does not exist");
                    //Intent intent = new Intent(emailVerifyActivity.this, SetupProfileActivity.class);
                    Intent intent = new Intent(emailVerifyActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "getUser:onCancelled", databaseError.toException());

            }
        });
    }
}