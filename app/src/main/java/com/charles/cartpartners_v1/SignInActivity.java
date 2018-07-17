package com.charles.cartpartners_v1;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.charles.cookingapp.R;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        final SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);


        /*setup background process (for notifications) if it is not setup yet. Note if user requests
            "Never" for notifications then we store a "-1" for sync_frequency
        */
        String marketPref = sharedPref.getString("sync_frequency", "30");
        Toast.makeText(this, "Sync Freq: " + marketPref, Toast.LENGTH_SHORT).show();
        boolean backgroundServiceActive = sharedPref.getBoolean("backgroundActive", false);
        if (!backgroundServiceActive && (!marketPref.equals("-1"))) {
            SharedPreferences.Editor writer = sharedPref.edit();
            writer.putBoolean("backgroundActive", true);
            writer.apply();

            ComponentName backgroundService = new ComponentName(this, BackgroundService.class);
            int jobID = 1234;
            JobInfo.Builder jobBuilder = new JobInfo.Builder(jobID, backgroundService);
            jobBuilder.setPersisted(true);
            //jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);
            long intervalDuration = Long.parseLong(marketPref);
            jobBuilder.setPeriodic(intervalDuration * 60000);
            //schedule the job
            JobScheduler scheduler = this.getSystemService(JobScheduler.class);
            scheduler.schedule(jobBuilder.build());
        }

        /*if the user has previously signed in and asked to be remembered, then skip asking for
         and open the main app */
        boolean rememberMe = sharedPref.getBoolean("userRememberMe", false);
        if (rememberMe) {
            Intent i = new Intent(this, MetricsView.class);
            startActivity(i);
            return;
        }

        final EditText emailInput = findViewById(R.id.input_email);
        final EditText passwordInput = findViewById(R.id.input_password);
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testEmail = emailInput.getText().toString();
                String testPassword = passwordInput.getText().toString();
                boolean loginSuccess = false;

                //TODO: delete this later, it's just so you can progress through dialogs
                if (testEmail.equals("charles") && testPassword.equals("password")) {
                    loginSuccess = true;
                }

                //TODO: check if login information is success. If so, take it and put it in sharedpreferences
                if (loginSuccess) {
                    SharedPreferences.Editor writer = sharedPref.edit();
                    //this is the Key we have for fetching from sharedPreferences the testPassword
                    writer.putString("userPassword", testPassword);
                    writer.putString("userEmail", testEmail);
                    //writer.putBoolean("userSignedIn", true);

                    Switch rememberMeSwitch = findViewById(R.id.remember_me_switch);
                    boolean isChecked = rememberMeSwitch.isChecked();
                    writer.putBoolean("userRememberMe", isChecked);
                    writer.apply();

                    Intent i = new Intent(view.getContext(), MetricsView.class);
                    startActivity(i);

                } else {
                    //do nothing
                }

            }
        });
    }

    //prevent user from pressing back on this page. Could allow them to "go backwards" from
    //signing out
    @Override
    public void onBackPressed() {
        //do nothing
    }

}
