package com.seven.zion.sitfolks;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class Student_LF extends Fragment {
    private EditText email_text;
    private EditText pass_text;
    private TextView reg;
    private Button sign_in;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private TextView forgotpass;


    public Student_LF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student__l, container, false);
        email_text = (EditText)view.findViewById(R.id.remail);
        pass_text = (EditText)view.findViewById(R.id.password);
        reg = (TextView)view.findViewById(R.id.register);
        sign_in = (Button)view.findViewById(R.id.button);
        progressDialog = new ProgressDialog(getActivity());
        mAuth = FirebaseAuth.getInstance();
        forgotpass = (TextView)view.findViewById(R.id.ForgotPass);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder forg = new AlertDialog.Builder(getActivity());
                forg.setTitle("Forgot Password");
                forg.setMessage("Are you sure to Reset password ?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String emailc = email_text.getText().toString().trim();
                        if (TextUtils.isEmpty(emailc))
                        {
                            Toast.makeText(getActivity(),"Please Enter Email !!",Toast.LENGTH_LONG).show();
                        }
                        else {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(emailc)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                                alertDialog.setMessage("Password Reset mail have been sent to your email, If you haven't received the Password reset mail" +
                                                        " then check your email and try again" ).setNegativeButton("ok",null).create().show();
                                            }
                                        }
                                    });

                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

            }
        });
        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setMessage("Application Under Constrtuction You can Still View Results by Pressing" +
                " Results Button at Top Right Corner").setNegativeButton("ok",null).create().show();*/

        // Inflate the layout for this fragment
        return view;

    }
    private void checkLogin()
    {
        String email = email_text.getText().toString().trim();
        String pass = pass_text.getText().toString().trim();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass))
        {
            progressDialog.setMessage("Logging In... ");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();

                    }
                    else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setMessage("Validation Failed ,Please Register Before Logging In Or Is the Email or" +
                                "Password is Incorrect ?" ).setNegativeButton("ok",null).create().show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(getActivity(),"Fields Cannot be Empty !!",Toast.LENGTH_LONG).show();
        }
    }

}
