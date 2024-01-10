package com.example.attendancetracker.Student.StdSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.attendancetracker.R;
import com.example.attendancetracker.SignUp.LoginPage;
import com.example.attendancetracker.Student.StudentHomePage.StudentHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class StudentSignup_1 extends AppCompatActivity{
    Context context;
    EditText email, password;
    Button signUp, cont;
    ProgressDialog pd;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_signup1);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_purple));

        context = getApplicationContext();
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        signUp = findViewById(R.id.signUp);
        cont = findViewById(R.id.cont);

        auth = FirebaseAuth.getInstance();

        cont.setVisibility(View.GONE);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) )
                    Toast.makeText(StudentSignup_1.this, "Enter you email and password", Toast.LENGTH_LONG).show();
                else{
                    pd.show();
                    pd.setMessage("Processing...");
                    auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                pd.dismiss();
                                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Email Verification Has Been Sent", Toast.LENGTH_SHORT).show();

                                        signUp.setVisibility(View.GONE);
                                        cont.setVisibility(View.VISIBLE);

                                        cont.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                auth.getCurrentUser().reload();
                                                if(auth.getCurrentUser().isEmailVerified()){
                                                    Intent i = new Intent(StudentSignup_1.this, StudentSignup_2.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    Toast.makeText(StudentSignup_1.this, "Verified Successfully", Toast.LENGTH_SHORT).show();

                                                    startActivity(i);
                                                }else{
                                                    Toast.makeText(StudentSignup_1.this, "Verify email first", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        auth.getCurrentUser().delete();
                                        startActivity(new Intent(StudentSignup_1.this, LoginPage.class));
                                        Toast.makeText(StudentSignup_1.this, "Could not sent email verification", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                auth.getCurrentUser().delete();
                                startActivity(new Intent(StudentSignup_1.this, LoginPage.class));
                                Toast.makeText(StudentSignup_1.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("well", "hoi");
    }
}