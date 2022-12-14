package com.example.thermalapplication;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.thermalapplication.databinding.ActivityLoginPageBinding;

public class LoginPage extends AppCompatActivity {

    private Button loginButton;
    private Button signUpButton;
    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        this.loginButton = (Button)findViewById(R.id.loginPageLoginButton);
        this.signUpButton = (Button)findViewById(R.id.loginPageSignUpButton);
        this.usernameInput = (EditText) findViewById(R.id.loginPageUsernameInput);
        this.passwordInput = (EditText) findViewById(R.id.loginPagePasswordInput);

        this.usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}