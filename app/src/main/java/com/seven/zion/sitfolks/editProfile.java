package com.seven.zion.sitfolks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class editProfile extends AppCompatActivity {
    private EditText Usname;
    private EditText Uroll;
    private EditText Usdept;
    private Button save;
    private String uid;
    private Button GD;
    DatabaseReference mReference;
    public String Usrname;
    public String rollno;
    public String dept;
    public String admin;
    public StudentDetails studentDetails ;
    ProgressDialog mProgess;

    public boolean validateR(String r)
    {
        if(r.length()<6)
            return false;
        int i = Integer.parseInt(r.substring(4));
        if(i>=0 && i<= 200)
            return true;
        else
            return false;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        uid = getIntent().getStringExtra(MainActivity.Uid_for_intent);
        Usname = (EditText)findViewById(R.id.Uname);
        Uroll = (EditText)findViewById(R.id.roll);
        Usdept = (EditText)findViewById(R.id.Udept);
        save = (Button)findViewById(R.id.e_button);
        GD = (Button)findViewById(R.id.gDetails);
        mProgess = new ProgressDialog(this);

        GD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        studentDetails = new StudentDetails();
                        studentDetails = dataSnapshot.getValue(StudentDetails.class);
                        Usrname = studentDetails.getName();
                        rollno = studentDetails.getRollno();
                        dept = studentDetails.getDepartment();
                        admin = studentDetails.getAdmin();
                        Log.d("msg",studentDetails.getName());
                        Usname.setText(Usrname);
                        Uroll.setText(rollno);
                        Usdept.setText(dept);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                SaveChanges();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(editProfile.this);
                builder.setTitle("Confirm !");
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).create().show();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    public void SaveChanges()
    {
        final String username = Usname.getText().toString().trim();
        final String department = Usdept.getText().toString().trim();
        final String roll = Uroll.getText().toString().trim();

        if (!TextUtils.isEmpty(username)&&!TextUtils.isEmpty(department)&& !TextUtils.isEmpty(roll))
        {
            mProgess.setMessage("Updating Please Wait🖐🖐" );
            mProgess.show();

            if(username.length()<4) {
                Toast.makeText(editProfile.this, "Username length Should be atleast 4 ", Toast.LENGTH_LONG).show();
                mProgess.dismiss();
            }
            else if (!validateR(roll))
            {
                Toast.makeText(editProfile.this,"Invalid Rollno",Toast.LENGTH_LONG).show();
                mProgess.dismiss();
            }
            else {
                            StudentDetails studentDetails = new StudentDetails(username,roll,department,admin);
                            mReference.setValue(studentDetails);
                            mProgess.dismiss();
                            Intent intent = new Intent(editProfile.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            this.finish();
                Toast.makeText(editProfile.this,"Successfully Changed",Toast.LENGTH_LONG).show();
            }

        }
        else
        {
            Toast.makeText(editProfile.this,"All Fields are Mandatory",Toast.LENGTH_LONG).show();
        }
    }

    }

