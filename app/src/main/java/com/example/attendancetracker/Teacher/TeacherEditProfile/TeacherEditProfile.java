package com.example.attendancetracker.Teacher.TeacherEditProfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.SignUp.LoginPage;
import com.example.attendancetracker.Student.StudentEditProfile.StudentEditProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherEditProfile extends AppCompatActivity {

    CircleImageView profileImage, editIcon;
    ImageView backbutton;
    EditText editName;
    TextView saveButton;
    ProgressDialog pd;
    String dp;
    FirebaseAuth auth;
    FirebaseUser firebaseCurrentUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_edit_profile);

        profileImage = findViewById(R.id.userDP);
        editIcon = findViewById(R.id.editIcon);
        editName = findViewById(R.id.editName);
        saveButton = findViewById(R.id.saveButton);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        backbutton = findViewById(R.id.backbutton);

        dp = getIntent().getStringExtra("imageID");

        auth = FirebaseAuth.getInstance();
        firebaseCurrentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Teacher");


        Glide.with(TeacherEditProfile.this).load(dp).into(profileImage);

        Query query = databaseReference.orderByChild("email").equalTo(firebaseCurrentUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String image = dataSnapshot1.child("imageID").getValue(String.class);
                    String name = dataSnapshot1.child("name").getValue(String.class);

                    try {
                        if(image != null)
                            Glide.with(TeacherEditProfile.this).load(image).into(profileImage);
                        editName.setText(name);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}});

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();

                map.put("name", editName.getText().toString());

                databaseReference.child(auth.getUid()).updateChildren(map);

                Toast.makeText(TeacherEditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if(data != null) {
                pd.show();
                pd.setMessage("Changing Profile Picture...");
                Uri imageUri = data.getData();
                Glide.with(TeacherEditProfile.this).load(imageUri).into(profileImage);

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("studentUpload").child(auth.getUid());

                if(imageUri != null){
                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    databaseReference.child(auth.getUid()).child("imageID").setValue(uri.toString());
                                    pd.dismiss();
                                    Toast.makeText(TeacherEditProfile.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout){
            auth.signOut();
            finish();
            startActivity(new Intent(TeacherEditProfile.this, LoginPage.class));
        }

        return super.onOptionsItemSelected(item);
    }
}