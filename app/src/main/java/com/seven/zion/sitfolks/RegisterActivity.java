package com.seven.zion.sitfolks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText usrname;
    EditText password;
    EditText cpassword;
    EditText dept;
    EditText rollno;
    EditText email;
    Button register;
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgess;

    public boolean validateR(String r)
    {
        if(r.length()<6)
            return false;
        int i = Integer.parseInt(r.substring(4));
        if(i>=0 && i<= 400)
            return true;
        else
            return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usrname = (EditText)findViewById(R.id.user_name);
        password = (EditText)findViewById(R.id.pass);
        cpassword = (EditText)findViewById(R.id.cpass);
        dept = (EditText)findViewById(R.id.depart);
        rollno = (EditText)findViewById(R.id.roll_no);
        email = (EditText)findViewById(R.id.remail);
        register = (Button)findViewById(R.id.RegisterB);
        mProgess = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usrname.getText().toString().trim();
                String pass1 = password.getText().toString().trim();
                String pass2 = cpassword.getText().toString().trim();
                final String department = dept.getText().toString().trim();
               final String roll = rollno.getText().toString().trim();
                String emaill = email.getText().toString().trim();


                if (!TextUtils.isEmpty(username)&& !TextUtils.isEmpty(pass1)&& !TextUtils.isEmpty(pass2)
                        && !TextUtils.isEmpty(department)&& !TextUtils.isEmpty(roll)&&!TextUtils.isEmpty(emaill))
                {
                    mProgess.setMessage("Registering Please Wait🖐🖐" );
                    mProgess.show();
                    if (!pass1.equals(pass2))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
                        alertDialog.setMessage("Passwords dosen't Match" ).setNegativeButton("ok",null).create().show();
                        mProgess.dismiss();

                    }
                    else if (!validateR(roll))
                    {
                        Toast.makeText(RegisterActivity.this,"Invalid Rollno",Toast.LENGTH_LONG).show();
                        mProgess.dismiss();
                    }
                    else if(pass1.length()<6 || pass2.length() <6) {
                        Toast.makeText(RegisterActivity.this, "Password's length Should be atleast 6 ", Toast.LENGTH_LONG).show();
                        mProgess.dismiss();
                    }
                    else {
                        mAuth.createUserWithEmailAndPassword(emaill,pass1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    String user_id = mAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user = mDatabase.child(user_id);
                                    StudentDetails studentDetails = new StudentDetails(username,roll,department,"false");
                                    current_user.setValue(studentDetails);
                                    mProgess.dismiss();
                                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);


                                }
                            }
                        });
                    }

                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"All Fields are Mandatory",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
