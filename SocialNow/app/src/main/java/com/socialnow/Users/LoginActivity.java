package com.socialnow.Users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.socialnow.API.API;
import com.socialnow.HomePage;
import com.socialnow.Models.Profile;
import com.socialnow.Models.User;
import com.socialnow.R;

import java.io.IOException;

/**
 * Created by lauamy on 23/10/15.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etUserName;
    EditText etPassword;
    Button btnLogin;
    Button btnSignup;
    Button btnForgotPassword;
    String userName;
    String password;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("");
        // Enable Local Datastore.
        context = this;

        setContentView(R.layout.activity_login);
        etUserName = (EditText) findViewById(R.id.et_Username);
        etPassword = (EditText) findViewById(R.id.et_PW);
        btnLogin = (Button) findViewById(R.id.bLogin);
        btnLogin.setOnClickListener(this);
        btnSignup = (Button) findViewById(R.id.bSignUp);
        btnSignup.setOnClickListener(this);
        btnForgotPassword = (Button) findViewById(R.id.bFpw);
        btnForgotPassword.setOnClickListener(this);


    }

    void isValidUser() {

        User u = new User();
        u.setEmail(etUserName.getText().toString());
        u.setPassword(etPassword.getText().toString());

        Response.Listener<User> response = new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {
                if (response.getId() != -1) {
                    Log.d("Login", "Login success " + response.getEmail() + " " + response.getName());

                    // Writing data to SharedPreferences
                    Utils.cacheUser(response);

                    Utils.setCurrentUser(true, response);

                    getProfileInfo();

                    Intent i2 = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(i2);
                    finish();
                } else {
                    Log.d("Login", "Error: " + response.getUser_token());
                    Log.d("Wrong credentials:", "Not valid username and password");
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                    dlgAlert.setMessage("Wrong password or username.");
                    dlgAlert.setTitle("Error Message");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    etUserName.setText("");
                                    etPassword.setText("");
                                }
                            });
                    dlgAlert.create().show();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Failed", "Login Failed");
                Utils.setCurrentUser(false, null);

            }
        };
        API.login("login", u, response, errorListener);


    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bLogin:
                userName = etUserName.getText().toString();
                password = etPassword.getText().toString();
                isValidUser();
                break;

            case R.id.bSignUp:
                Intent i2 = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i2);
                break;

            case R.id.bFpw:
                Intent i = new Intent(getApplicationContext(), ForgetPassword.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    public static void getProfileInfo() {
        Response.Listener<Profile> response = new Response.Listener<Profile>() {
            @Override
            public void onResponse(Profile response) {
                if (response.getName() != null) {
                    Log.d("Profile", "success " + response.getEmail() + " " + response.getName());

                    // Writing data to SharedPreferences
                    Utils.cacheProfile(response);

                }else{
                    Log.d("Login", "Error: " + response.getUser_token());
                    Log.d("Wrong credentials:", "Not valid username and password");
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Failed", "Login Failed");

            }
        };
        API.profileInfo("profile", Utils.getCurrentUser().getUser_token(), response, errorListener);
    }
}
