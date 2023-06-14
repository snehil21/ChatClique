package com.example.chatclique;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {
    TextView txtLogin;
    EditText rg_username,rg_email,rg_password,rg_repassword;
    Button signup;
    CircleImageView rg_profileImg;
    FirebaseAuth auth;
    Uri imageURI;
    String imageuri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        txtLogin=findViewById(R.id.txtGoLogin);
        rg_username=findViewById(R.id.txtUserName);
        rg_email=findViewById(R.id.TxtEmail);
        rg_password=findViewById(R.id.TxtPassword);
        rg_repassword=findViewById(R.id.TxtCnfPassword);
        signup = findViewById(R.id.btnSignUp);
        rg_profileImg=findViewById(R.id.profilerg);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(registration.this,login.class);
                startActivity(intent);
                finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=rg_username.getText().toString();
                String email=rg_email.getText().toString();
                String password=rg_password.getText().toString();
                String cnfPassword=rg_repassword.getText().toString();
                String status="Hey I'm using ChatClique";
                String emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email)|| TextUtils.isEmpty(password)
                        ||TextUtils.isEmpty(cnfPassword)){
                    Toast.makeText(registration.this,"Please Enter valid input",Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)) {
                    rg_email.setError("Type a valid Email.");
                }else if(password.length()<6){
                    rg_password.setError("Password must contains minimum 6 characters.");
                }else if(!password.equals(cnfPassword)){
                    rg_password.setError("Re Entered password does not match");
                }else{
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String id=task.getResult().getUser().getUid();
                                DatabaseReference reference=database.getReference().child("user").child("id");
                                StorageReference storageReference=storage.getReference().child("upload").child("id");

                                if(imageURI!=null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri=uri.toString();
                                                        Users users=new Users(id,name,email,password,imageuri,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Intent intent=new Intent(registration.this,MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(registration.this,"Error in creating the account",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {
                                    String status="Hey I'm using ChatClique";
                                    imageuri="https://firebasestorage.googleapis.com/v0/b/chatclique-9ef01.appspot.com/o/man.png?alt=media&token=4ef7a59f-e997-4f6b-9625-3ffbe17bbb4f";
                                    Users users=new Users(id,name,email,password,imageuri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent=new Intent(registration.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(registration.this,"Error in creating the account",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else{
                                Toast.makeText(registration.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        rg_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(data!=null){
                imageURI=data.getData();
                rg_profileImg.setImageURI(imageURI);
            }
        }
    }
}