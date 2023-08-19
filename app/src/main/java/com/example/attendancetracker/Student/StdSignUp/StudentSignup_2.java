package com.example.attendancetracker.Student.StdSignUp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentHomePage.StudentHome;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
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

public class StudentSignup_2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText name, email, rollnumber;
    Spinner dropdown;
    TextView department;
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
        setContentView(R.layout.activity_student_signup2);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        name = findViewById(R.id.name);
        rollnumber = findViewById(R.id.rollnumber);
        department = findViewById(R.id.department);
        dropdown = findViewById(R.id.dropdown_menu);
        profileImage = findViewById(R.id.userDP);
        editIcon = findViewById(R.id.editDP);

        signUp = findViewById(R.id.signUp);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://attendancetracker-1f116-default-rtdb.firebaseio.com");
        storage = FirebaseStorage.getInstance();

        StudentSignup_2.CustomAdapter<String> adapter = new StudentSignup_2.CustomAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[] {"CSE", "IT", "ECE", "EE", "AEIE"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dropdown.setOnItemSelectedListener(this);

        dropdown.setAdapter(adapter);

        department.setText(dropdown.getSelectedItem().toString());

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
                    Toast.makeText(StudentSignup_2.this, "Set your profile picture", Toast.LENGTH_SHORT).show();
                else if(TextUtils.isEmpty(name.getText().toString()))
                    Toast.makeText(StudentSignup_2.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(rollnumber.getText().toString())) {
                    Toast.makeText(StudentSignup_2.this, "Enter Roll number", Toast.LENGTH_SHORT).show();
                } else{
                    pd.show();
                    pd.setMessage("Creating account...");

                    DatabaseReference databaseReference = database.getReference().child("student").child(dropdown.getSelectedItem().toString()).child(auth.getUid());
                    StorageReference storageReference = storage.getReference().child("studentUpload").child(auth.getUid());

                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        image = uri.toString();

                                        StudentModel studentModel = new StudentModel(auth.getUid(), name.getText().toString(), auth.getCurrentUser().getEmail(), image, dropdown.getSelectedItem().toString(), rollnumber.getText().toString(), classes);

                                        databaseReference.setValue(studentModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    pd.dismiss();
                                                    startActivity(new Intent(StudentSignup_2.this, StudentHome.class));
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        Toast.makeText(StudentSignup_2.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        department.setText(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private static class CustomAdapter<T> extends ArrayAdapter<String> {
        public CustomAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText("");
            return view;
        }
    }
}