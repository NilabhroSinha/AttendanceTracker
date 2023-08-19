package com.example.attendancetracker.Teacher.TeachSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.TeacherHomePage.TeacherHome;
import com.example.attendancetracker.Teacher.TeacherModel.TeacherModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class TeacherSignUp_2 extends AppCompatActivity {
    EditText Fname, Lname;
    Button signUp;
    ImageView profileImage, editIcon;
    Uri imageURI = null;
    ProgressDialog pd;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String image;
    ArrayList<String> classes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_sign_up2);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        Fname = findViewById(R.id.Fname);
        Lname = findViewById(R.id.Lname);
        profileImage = findViewById(R.id.userDP);
        editIcon = findViewById(R.id.editDP);

        signUp = findViewById(R.id.signUp);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://attendancetracker-1f116-default-rtdb.firebaseio.com");
        storage = FirebaseStorage.getInstance();

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageURI == null)
                    Toast.makeText(TeacherSignUp_2.this, "Set your profile picture", Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(Fname.getText().toString()))
                    Toast.makeText(TeacherSignUp_2.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(Lname.getText().toString())) {
                    Toast.makeText(TeacherSignUp_2.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                } else{
                    pd.show();
                    pd.setMessage("Creating account...");

                    DatabaseReference databaseReference = database.getReference().child("Teacher").child(auth.getUid());
                    StorageReference storageReference = storage.getReference().child("TeacherUpload").child(auth.getUid());

                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        image = uri.toString();

                                        TeacherModel teacherModel = new TeacherModel(auth.getUid(), Fname.getText().toString() + Lname.getText().toString(), auth.getCurrentUser().getEmail(), image, classes);

                                        databaseReference.setValue(teacherModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    pd.dismiss();
                                                    startActivity(new Intent(TeacherSignUp_2.this, TeacherHome.class));
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(TeacherSignUp_2.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10){
            if(data != null){
                imageURI = data.getData();
                profileImage.setImageURI(imageURI);
            }

        }
    }
}