package com.seven.zion.sitfolks;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmDialog extends DialogFragment implements View.OnClickListener {
    private CheckBox checkBox;
    private Button button1;
    private TextView msg;
    private TextView subject;
    private TextView grade;
    private EditText credits;
    private EditText Gpoints;
    public boolean notice = false;
    public boolean cgpa = false;
    public double points;
    public Double totalcp =0.00;
    public Double tCPGP = 0.00;
    public int number;
    public Double creditP= 0.00;
    public String GradeP;
    String mes = "null";
    private onCompleteList mListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.dialog, container);
        checkBox = (CheckBox) mainView.findViewById(R.id.checkBox);
        button1 = (Button) mainView.findViewById(R.id.button1);
        msg = (TextView)mainView.findViewById(R.id.Dinfo);
        grade =(TextView)mainView.findViewById(R.id.grade);
        Gpoints = (EditText)mainView.findViewById(R.id.gp);
        credits = (EditText)mainView.findViewById(R.id.credit);
        subject = (TextView)mainView.findViewById(R.id.subj);

        String grademsg = "Grade For Sub "+ number;
        String message = "Credit For Sub " + number;

        subject.setText(message);
        grade.setText(grademsg);

        if(notice == true) {
            grade.setVisibility(View.GONE);
            Gpoints.setVisibility(View.GONE);
            credits.setVisibility(View.GONE);
            subject.setVisibility(View.GONE);
        }
        if (cgpa==true)
        {
            checkBox.setVisibility(View.GONE);
            msg.setVisibility(View.GONE);
        }
        button1.setOnClickListener(this);
        msg.setText(mes);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Store the isChecked to Preference here
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("DONT_SHOW_DIALOG", isChecked);
                editor.commit();

            }
        });
        return mainView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                if(cgpa)
                {
                    GradeP = Gpoints.getText().toString().trim();
                    creditP = Double.parseDouble(credits.getText().toString().trim());
                    totalcp += creditP;
                    if(GradeP.equals("O")||GradeP.equals("o"))
                        tCPGP += (10 * creditP);
                    else if (GradeP.equals("A+")||GradeP.equals("a+"))
                        tCPGP += (9 * creditP);
                    else if (GradeP.equals("A")||GradeP.equals("a"))
                        tCPGP += (8*creditP);
                    else if (GradeP.equals("B+")||GradeP.equals("b+"))
                        tCPGP += (7*creditP);
                    else if (GradeP.equals("B")||GradeP.equals("b"))
                        tCPGP += (6*creditP);

                    Log.d("GradeP",Double.toString(tCPGP));
                    this.mListener.gettGPcp(totalcp,tCPGP);
                }
                    dismiss();
                break;
        }
    }
    public void setMessage(String message)
    {
        mes = message;
    }
    public void setNotice(boolean val)
    {
        notice = val;
    }
    public void setCGPA(boolean val)
    {
        cgpa = val;
    }
    public void setGP(int number)
    {
        this.number = number;

    }

    public interface onCompleteList{
        public void gettGPcp(Double totalcp,Double totalCPGP);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (onCompleteList) activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }
}
