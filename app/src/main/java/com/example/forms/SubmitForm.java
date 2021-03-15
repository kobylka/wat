package com.example.forms;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SubmitForm extends androidx.fragment.app.Fragment
{
    public SubmitForm() {
        super(R.layout.activity_submit);
    }

    private String json;
    private FormClass form;
    private String apiID;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        json = bundle.getString("json");
        form = (FormClass) bundle.getSerializable("form");
        apiID = bundle.getString("apiID");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        Context globalContext=getActivity();

        LinearLayout ll = (LinearLayout)view.findViewById(R.id.submit_layout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    URL url = new URL("https://" + apiID + ".form.io/" + form.getName() + "/submission");
                    HttpsURLConnection http = (HttpsURLConnection)url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoOutput(true);
                    http.setRequestProperty("Content-Type", "application/json");

                    byte[] out = json.getBytes(StandardCharsets.UTF_8);

                    OutputStream stream = http.getOutputStream();
                    stream.write(out);

                    Log.d("TAG", json);
                    Log.i("TAG", http.getResponseCode() + " " + http.getResponseMessage());
                    http.disconnect();

                    String msg1 = "Answers sent successfully";
                    String msg2 = json;
                    if(http.getResponseCode() != 201)
                    {
                        msg1 = "Failed to send the answers";
                        msg2 = "Form incomplete";
                    }

                    final TextView text = new TextView(globalContext);
                    text.setPadding(0,20,0,0);
                    text.setText(msg1);
                    text.setGravity(Gravity.CENTER);
                    text.setTextColor(Color.BLACK);
                    text.setTextSize(20);

                    final TextView text2 = new TextView(globalContext);
                    text2.setText(msg2);
                    text2.setGravity(Gravity.CENTER);
                    text2.setTextColor(Color.BLACK);
                    text2.setTextSize(20);

                    Button again = new Button(globalContext);
                    again.setText("Submit again");
                    again.setGravity(Gravity.CENTER);
                    again.setOnClickListener(new View.OnClickListener()
                    {
                        public void onClick(View v)
                        {
                            Fragment fragment = null;
                            Class fragmentClass;
                            fragmentClass = FillForm.class;

                            try
                            {
                                fragment = (Fragment) fragmentClass.newInstance();
                                Bundle args = new Bundle();
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

                    ((Activity) globalContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            ll.addView(text, lp);
                            ll.addView(text2, lp);
                            ll.addView(again, lp);
                        }
                    });
                }
                catch (Exception e) { e.printStackTrace(); }
            }
        });
        thread.start();
    }
}
