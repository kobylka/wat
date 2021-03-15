package com.example.forms;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;
import java.util.Map;

public class FillForm extends androidx.fragment.app.Fragment
{
    public FillForm() {
        super(R.layout.activity_fill);
    }

    private String apiID;

    private FormClass form;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        form = (FormClass) bundle.getSerializable("form");
        apiID = bundle.getString("apiID");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Toolbar toolbar = (Toolbar) ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar);
        toolbar.setTitle(form.getName());
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        List<Question> questions = form.getQuestions();
        if(questions != null)
        {
            Context globalContext=getActivity().getApplicationContext();

            LinearLayout ll = (LinearLayout)view.findViewById(R.id.fill_layout);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            TextView text = new TextView(globalContext);
            text.setText("Form: " + form.getName());
            text.setGravity(Gravity.CENTER);
            text.setTextColor(Color.BLACK);
            text.setTextSize(20);
            lp.setMargins(0, 0, 0, 20);
            ll.addView(text, lp);

            int i = 1;
            for(Question q : questions)
            {
                Map<String, String> values = q.processQuestions();

                if(q.getType().equals("radio"))
                {
                    text = new TextView(globalContext.getApplicationContext());
                    text.setText(String.valueOf(i++) + ". " + q.getKey());
                    text.setTextSize(18);
                    text.setPadding(0, 20, 0, 0);
                    text.setTextColor(Color.BLACK);
                    ll.addView(text, lp);

                    RadioButton[] rb = new RadioButton[2];
                    RadioGroup rg = new RadioGroup(globalContext); //create the RadioGroup
                    rg.setTag(q.getKey());
                    rg.setOrientation(RadioGroup.HORIZONTAL);//or RadioGroup.VERTICAL
                    int j = 0;
                    for (String k : values.keySet())
                    {
                        rb[j]  = new RadioButton(globalContext);
                        rg.addView(rb[j]);
                        rb[j].setButtonTintMode(PorterDuff.Mode.DARKEN);
                        rb[j].setText(k);
                        rb[j].setTag(values.get(k));
                        rb[j++].setTextColor(Color.BLACK);
                    }
                    ll.addView(rg, lp);
                }
            }

            Button submit = new Button(globalContext);
            submit.setText("Submit");
            submit.setTextSize(20);
            submit.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    String jsonInputString = "{\"data\": {";

                    List<Question> questions = form.getQuestions();
                    for(Question q : questions)
                    {
                        Map<String, String> values = q.processQuestions();

                        if (q.getType().equals("radio"))
                        {
                            jsonInputString += "\"" + q.getKey()+"\": ";
                            RadioGroup rg = ll.findViewWithTag(q.getKey());
                            jsonInputString += "\"";
                            for(int i = 0; i < rg.getChildCount(); ++i)
                            {
                                RadioButton b = (RadioButton) rg.getChildAt(i);
                                if(b.isChecked()) jsonInputString += b.getTag();
                            }
                            jsonInputString += "\",";
                        }
                    }
                    jsonInputString = jsonInputString.substring(0, jsonInputString.length()-1) + "}}";

                    Fragment fragment = null;
                    Class fragmentClass;
                    fragmentClass = SubmitForm.class;

                    try
                    {
                        fragment = (Fragment) fragmentClass.newInstance();
                        Bundle args = new Bundle();
                        args.putString("json", jsonInputString);
                        args.putSerializable("form", form);
                        args.putString("apiID", apiID);
                        fragment.setArguments(args);
                    }
                    catch (Exception e) { e.printStackTrace(); }

                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }
            });
            ll.addView(submit, lp);
        }
    }
}
