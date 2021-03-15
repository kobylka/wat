package com.example.forms;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class ShowSubmissions extends Fragment
{
    private ArrayList<FormClass> forms = new ArrayList<>();
    private String apiID;

    public ShowSubmissions() { super(R.layout.activity_browse); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        forms = (ArrayList<FormClass>) bundle.getSerializable("forms");
        apiID = bundle.getString("apiID");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Context globalContext=getActivity();

        TextView header = (TextView) view.findViewById(R.id.header);

        if(forms.size() > 0) header.setText("Submissions of:");
        else header.setText("No available forms");

        for(FormClass f : forms)
        {
            Button formButton = new Button(globalContext.getApplicationContext());
            formButton.setText(f.getName());
            formButton.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Fragment fragment = null;
                    Class fragmentClass;
                    fragmentClass = FormSubmissions.class;

                    try
                    {
                        fragment = (Fragment) fragmentClass.newInstance();
                        Bundle args = new Bundle();
                        args.putSerializable("form", f);
                        args.putSerializable("forms", forms);
                        args.putString("apiID", apiID);
                        fragment.setArguments(args);
                    }
                    catch (Exception e) { e.printStackTrace(); }

                    // Insert the fragment by replacing any existing fragment
                    FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
                }
            });

            LinearLayout ll = (LinearLayout)view.findViewById(R.id.browser_layout);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.addView(formButton, lp);
        }
    }
}
