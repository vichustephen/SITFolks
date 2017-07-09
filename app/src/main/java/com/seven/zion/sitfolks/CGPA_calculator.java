package com.seven.zion.sitfolks;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CGPA_calculator extends AppCompatActivity implements ConfirmDialog.onCompleteList {
    private TextView gpa;
    private EditText resgpa;
    private Button calculate;
    private Button add;
    public Double totalCp= 0.00;
    public Double tGPcp= 0.00;
    private Button hintB;
    int i = 0;
    public boolean ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cgpa_calculator);
        gpa = (TextView)findViewById(R.id.getgpa);
        resgpa = (EditText)findViewById(R.id.resultG);
        calculate = (Button)findViewById(R.id.CalcB);
        add = (Button)findViewById(R.id.AddBtn);
        hintB = (Button)findViewById(R.id.hint) ;
        resgpa.setFocusable(false);
        hintB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder hint = new AlertDialog.Builder(CGPA_calculator.this);
                hint.setTitle("Hint");
                hint.setMessage("Press the Add button to add a subject with it's Credits and grade.,Ex:Grades = O,A,B+, Credits = 1,2,4"+
                " Add all subjects one by one, then finally click Calculate Button").setNegativeButton("OK",null).create().show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     i++;
                resgpa.setText("0.0");
                    ConfirmDialog CCGPAdialog = new ConfirmDialog();
                        CCGPAdialog.setCGPA(true);
                        CCGPAdialog.setGP(i);
                        Dialog dialogFrg=CCGPAdialog.getDialog();
                        if(dialogFrg!=null && dialogFrg.isShowing()) {
                            // no need to call dialog.show
                        } else {
                            CCGPAdialog.show(getFragmentManager(), "Subjects");
                        }
                  gpa.setText(Integer.toString(i));
                Log.d("tag",Double.toString(tGPcp));
                Log.d("gpa",Double.toString(totalCp));
            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double finalAns = tGPcp/totalCp;
                resgpa.setText(Double.toString(finalAns));
                gpa.setText("0");
                i=0;
                totalCp = tGPcp = 0.00;

            }
        });
    }

    @Override
    public void gettGPcp(Double totalcp, Double totalCPGP) {
        tGPcp += totalCPGP;
        totalCp += totalcp;
    }
}
