package com.example.thermalapplication;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.thermalapplication.databinding.ActivityLoginPageBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import helpers.RegularExpressions;
import repository.firestore.manager.UserFirestoreManager;

public final class LoginPage extends AppCompatActivity {

    final String[] UsernameValidationMessages = new String[] {
            "\u2022 Username must contain at least 7 characters",
            "\u2022 Username must contain at least one digit",
            "\u2022 Username must contain at least 1 symbol",
            "\u2022 Username must contain at least 1 uppercase letter",
            "\u2022 Username must contain at least 1 lowercase letter"
    };

    public Button loginButton;
    public Button signUpButton;
    public EditText usernameInput;
    public EditText passwordInput;
    public LinearLayout formErrors;
    public static volatile int[] ids;
    public static volatile boolean[] displaying;
    public static UserFirestoreManager userFirestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        this.loginButton = (Button)findViewById(R.id.loginPageLoginButton);
        this.signUpButton = (Button)findViewById(R.id.loginPageSignUpButton);
        this.usernameInput = (EditText) findViewById(R.id.loginPageUsernameInput);
        this.passwordInput = (EditText) findViewById(R.id.loginPagePasswordInput);
        this.formErrors = (LinearLayout) findViewById(R.id.loginPageFormErrors);
        userFirestoreManager = new UserFirestoreManager(FirebaseFirestore.getInstance());
        ids = new int[]{ View.generateViewId(), View.generateViewId(), View.generateViewId(), View.generateViewId(), View.generateViewId() };
        displaying = new boolean[] { false, false, false, false, false };

        this.usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void afterTextChanged(Editable s) {
                boolean[] usernameValidation = RegularExpressions.ValidateUsername(s.toString());
                boolean foundErrors = false;
                boolean doesUsernameAlreadyExist = s.toString().length() > 0 && userFirestoreManager.doesUserExist(s.toString());
                //System.out.printf("%s\n", doesUsernameAlreadyExist ? "User exists": "User does not exist");
                for (int i = 0; s.toString().length() > 0 && i < usernameValidation.length; i++) {
                    if (usernameValidation[i] && !foundErrors) {
                        foundErrors = true;
                    }
                    if (usernameValidation[i] && !displaying[i]) {
                        TextView userError = new TextView(LoginPage.this);
                        userError.setText(UsernameValidationMessages[i]);
                        userError.setTextColor(Color.parseColor("#FF0000"));
                        userError.setId(ids[i]);
                        displaying[i] = true;
                        ((LinearLayout) findViewById(R.id.loginPageFormErrors)).addView(userError);
                    } else {
                        if (displaying[i] && !usernameValidation[i]) {
                            TextView foundView = findViewById(ids[i]);
                            ((ViewGroup) foundView.getParent()).removeView(foundView);
                            displaying[i] = false;
                        }
                    }
                }
                if (!foundErrors && s.toString().length() > 0) {
                    TextView loginFormUsernameTitle = (TextView)findViewById(R.id.loginFormUsernameTitle);
                    loginFormUsernameTitle.setTextColor(Color.parseColor("#4CAF50"));
                    loginFormUsernameTitle.setText("Valid Username!");
                } else if (foundErrors && s.toString().length() > 0) {
                    TextView loginFormUsernameTitle = (TextView)findViewById(R.id.loginFormUsernameTitle);
                    loginFormUsernameTitle.setTextColor(Color.parseColor("#FF0000"));
                    loginFormUsernameTitle.setText("Invalid Username!");
                } else {
                    TextView loginFormUsernameTitle = (TextView)findViewById(R.id.loginFormUsernameTitle);
                    if (loginFormUsernameTitle.getText().toString().equalsIgnoreCase("Valid Username!") || loginFormUsernameTitle.getText().toString().equalsIgnoreCase("Invalid Username!")) {
                        loginFormUsernameTitle.setText("Username");
                        loginFormUsernameTitle.setTextColor(R.color.black);
                    }
                    if (!foundErrors && s.toString().length() == 0) {
                        System.out.println("In if");
                        for (int i = 0; i < ids.length; i++) {
                            if (displaying[i]) {
                                TextView foundView = findViewById(ids[i]);
                                ((ViewGroup) foundView.getParent()).removeView(foundView);
                                displaying[i] = false;
                            }
                        }
                    }
                }
            }
        });
    }

}