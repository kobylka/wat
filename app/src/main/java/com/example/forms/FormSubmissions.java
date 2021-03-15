package com.example.forms;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FormSubmissions extends androidx.fragment.app.Fragment
{
    private String apiID;
    private FormClass form;
    private ArrayList<FormClass> forms = new ArrayList<>();

    public FormSubmissions() { super(R.layout.activity_browse); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        form = (FormClass) bundle.getSerializable("form");
        forms = (ArrayList<FormClass>) bundle.getSerializable("forms");
        apiID = bundle.getString("apiID");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Toolbar toolbar = (Toolbar) ((AppCompatActivity)getActivity()).findViewById(R.id.toolbar);
        toolbar.setTitle(form.getName() + ": submissions");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ArrayList<JSONObject> answersList = new ArrayList<JSONObject>();

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    InputStream is = new URL("https://" + apiID + ".form.io/" + form.getName() + "/submission?limit=100000").openStream();
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                    String content = convertStreamToString(is);
                    JSONArray jArray = new JSONArray(content);
                    if (jArray != null)
                    {
                        for (int i=0;i<jArray.length();i++)
                        {
                            String s = jArray.getString(i);
                            s = s.substring(s.indexOf("data")+6);
                            s = s.substring(0, s.indexOf("}")+1);
                            answersList.add(new JSONObject(s));
                        }
                    }
                }
                catch (MalformedURLException e) { e.printStackTrace(); }
                catch (IOException e) { e.printStackTrace(); }
                catch (JSONException e) { e.printStackTrace(); }
            }
        });
        thread.start();
        try{ thread.join(); } catch(Exception e){}

        Context globalContext=getActivity();

        TextView tv = view.findViewById(R.id.header);
        tv.setText(answersList.toString());

        Button ret = new Button(globalContext);
        ret.setText("Return");
        ret.setGravity(Gravity.CENTER);
        ret.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Fragment fragment = null;
                Class fragmentClass = ShowSubmissions.class;

                try
                {
                    fragment = (Fragment) fragmentClass.newInstance();
                    Bundle args = new Bundle();
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
        ll.addView(ret, lp);
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
