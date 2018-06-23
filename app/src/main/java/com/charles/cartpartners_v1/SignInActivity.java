package com.charles.cartpartners_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.charles.cookingapp.R;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        final SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);

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
                    writer.putBoolean("userSignedIn", true);
                    writer.apply();

                    Intent i = new Intent(view.getContext(), MainActivity.class);
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
