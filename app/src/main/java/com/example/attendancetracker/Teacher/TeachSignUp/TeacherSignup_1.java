package com.example.attendancetracker.Teacher.TeachSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.attendancetracker.R;
import com.example.attendancetracker.SignUp.LoginPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class TeacherSignup_1 extends AppCompatActivity {
    EditText email, password;
    Button signUp;
    ProgressDialog pd;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_signup1);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);


        signUp = findViewById(R.id.signUp);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://attendancetracker-1f116-default-rtdb.firebaseio.com");
        storage = FirebaseStorage.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) )
                    Toast.makeText(TeacherSignup_1.this, "Enter you email and password", Toast.LENGTH_LONG).show();
                else{
                    pd.show();
                    pd.setMessage("Waiting for verification...");

                    auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(TeacherSignup_1.this, "Email Verification Has Been Sent", Toast.LENGTH_SHORT).show();

                                        while(!auth.getCurrentUser().isEmailVerified()){
                                            auth.getCurrentUser().reload();
                                            try {
                                                Thread.sleep(3000);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        pd.dismiss();
                                        Toast.makeText(TeacherSignup_1.this, "Verified Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(new Intent(TeacherSignup_1.this, TeacherSignUp_2.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        auth.getCurrentUser().delete();
                                        startActivity(new Intent(TeacherSignup_1.this, LoginPage.class));
                                        Toast.makeText(TeacherSignup_1.this, "Could not sent email verification", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else{
                                auth.getCurrentUser().delete();
                                startActivity(new Intent(TeacherSignup_1.this, LoginPage.class));
                                Toast.makeText(TeacherSignup_1.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


}