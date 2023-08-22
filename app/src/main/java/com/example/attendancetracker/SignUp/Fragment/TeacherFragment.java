package com.example.attendancetracker.SignUp.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentHomePage.StudentHome;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.TeacherHomePage.TeacherHome;
import com.example.attendancetracker.Teacher.TeachSignUp.TeacherSignup_1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherFragment extends Fragment {
    EditText email, pass;
    TextView signup;
    Button button;
    FirebaseAuth auth;
    ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_login_layout, null);

        email = view.findViewById(R.id.techEmailET);
        pass = view.findViewById(R.id.teachPassET);
        button = view.findViewById(R.id.teachLogin);
        signup = view.findViewById(R.id.teachsignuphere);


        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);

        auth = FirebaseAuth.getInstance();

        if(auth.getUid() != null)
            ifAlreadyPresent();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TeacherSignup_1.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(pass.getText().toString()) )
                    Toast.makeText(getContext(), "Enter you email and password", Toast.LENGTH_LONG).show();
                else{
                    pd.show();
                    pd.setMessage("Signing in...");
                    auth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                startActivity(new Intent(getContext(), TeacherHome.class));
                                pd.dismiss();
                                getActivity().finish();
                            }
                            else {
                                pd.dismiss();
                                Toast.makeText(getContext(), "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        return view;
    }
    private void ifAlreadyPresent() {
        FirebaseDatabase.getInstance().getReference().child("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Intent i;
                if(snapshot.hasChild(auth.getUid())){
                    i = new Intent(getContext(), TeacherHome.class);

                }else{
                    i = new Intent(getContext(), StudentHome.class);
                }

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
