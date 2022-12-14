package com.example.thermalapplication;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

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

    final String[] PasswordValidationMessages = new String[] {
            "\u2022 Password must contain at least 7 characters",
            "\u2022 Password must contain at least one digit",
            "\u2022 Password must contain at least 1 symbol",
            "\u2022 Password must contain at least 1 uppercase letter",
            "\u2022 Password must contain at least 1 lowercase letter"
    };

    public Button loginButton;
    public Button signUpButton;
    public EditText usernameInput;
    public EditText passwordInput;
    public LinearLayout formErrors;
    public static UserFirestoreManager userFirestoreManager;
    public static volatile int[] usernameIds;
    public static volatile boolean[] displayingUsernameErrors;
    public static volatile boolean usernameValid = false;

    public static volatile int[] passwordIds;
    public static volatile boolean[] displayingPasswordErrors;
    public static volatile boolean passwordValid = false;

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
        usernameIds = new int[]{ View.generateViewId(), View.generateViewId(), View.generateViewId(), View.generateViewId(), View.generateViewId() };
        displayingUsernameErrors = new boolean[] { false, false, false, false, false };

        passwordIds = new int[]{ View.generateViewId(), View.generateViewId(), View.generateViewId(), View.generateViewId(), View.generateViewId() };
        displayingPasswordErrors = new boolean[] { false, false, false, false, false };


        this.passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean[] passwordValidation = RegularExpressions.ValidatePassword(s.toString());
                boolean foundErrors = false;
                for (int i = 0; s.toString().length() > 0 && i < passwordValidation.length; i++) {
                    if (passwordValidation[i] && !foundErrors) {
                        foundErrors = true;
                    }
                    if (passwordValidation[i] && !displayingPasswordErrors[i]) {
                        TextView userError = new TextView(LoginPage.this);
                        userError.setText(PasswordValidationMessages[i]);
                        userError.setTextColor(Color.parseColor("#FF0000"));
                        userError.setId(passwordIds[i]);
                        displayingPasswordErrors[i] = true;
                        ((LinearLayout) findViewById(R.id.loginPageFormErrors)).addView(userError);
                    } else {
                        if (displayingPasswordErrors[i] && !passwordValidation[i]) {
                            TextView foundView = findViewById(passwordIds[i]);
                            ((ViewGroup) foundView.getParent()).removeView(foundView);
                            displayingPasswordErrors[i] = false;
                        }
                    }
                }
                if (!foundErrors && s.toString().length() > 0) {
                    TextView loginFormPasswordTitle = (TextView)findViewById(R.id.loginFormPasswordTitle);
                    loginFormPasswordTitle.setTextColor(Color.parseColor("#4CAF50"));
                    loginFormPasswordTitle.setText("Valid Password!");
                    usernameValid = true;
                } else if (foundErrors && s.toString().length() > 0) {
                    TextView loginFormPasswordTitle = (TextView)findViewById(R.id.loginFormPasswordTitle);
                    loginFormPasswordTitle.setTextColor(Color.parseColor("#FF0000"));
                    loginFormPasswordTitle.setText("Invalid Password!");
                } else {
                    TextView loginFormPasswordTitle = (TextView)findViewById(R.id.loginFormPasswordTitle);
                    if (loginFormPasswordTitle.getText().toString().equalsIgnoreCase("Valid Password!") || loginFormPasswordTitle.getText().toString().equalsIgnoreCase("Invalid Password!")) {
                        loginFormPasswordTitle.setText("Password");
                        loginFormPasswordTitle.setTextColor(Color.parseColor("#FF000000"));
                    }
                    if (!foundErrors && s.toString().length() == 0) {
                        for (int i = 0; i < passwordIds.length; i++) {
                            if (displayingPasswordErrors[i]) {
                                TextView foundView = findViewById(passwordIds[i]);
                                ((ViewGroup) foundView.getParent()).removeView(foundView);
                                displayingPasswordErrors[i] = false;
                            }
                        }
                    }
                }
            }
        });

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
                    if (usernameValidation[i] && !displayingUsernameErrors[i]) {
                        TextView userError = new TextView(LoginPage.this);
                        userError.setText(UsernameValidationMessages[i]);
                        userError.setTextColor(Color.parseColor("#FF0000"));
                        userError.setId(usernameIds[i]);
                        displayingUsernameErrors[i] = true;
                        ((LinearLayout) findViewById(R.id.loginPageFormErrors)).addView(userError);
                    } else {
                        if (displayingUsernameErrors[i] && !usernameValidation[i]) {
                            TextView foundView = findViewById(usernameIds[i]);
                            ((ViewGroup) foundView.getParent()).removeView(foundView);
                            displayingUsernameErrors[i] = false;
                        }
                    }
                }
                if (!foundErrors && s.toString().length() > 0) {
                    TextView loginFormUsernameTitle = (TextView)findViewById(R.id.loginFormUsernameTitle);
                    loginFormUsernameTitle.setTextColor(Color.parseColor("#4CAF50"));
                    loginFormUsernameTitle.setText("Valid Username!");
                    usernameValid = true;
                } else if (foundErrors && s.toString().length() > 0) {
                    TextView loginFormUsernameTitle = (TextView)findViewById(R.id.loginFormUsernameTitle);
                    loginFormUsernameTitle.setTextColor(Color.parseColor("#FF0000"));
                    loginFormUsernameTitle.setText("Invalid Username!");
                } else {
                    TextView loginFormUsernameTitle = (TextView)findViewById(R.id.loginFormUsernameTitle);
                    if (loginFormUsernameTitle.getText().toString().equalsIgnoreCase("Valid Username!") || loginFormUsernameTitle.getText().toString().equalsIgnoreCase("Invalid Username!")) {
                        loginFormUsernameTitle.setText("Username");
                        loginFormUsernameTitle.setTextColor(Color.parseColor("#FF000000"));
                    }
                    if (!foundErrors && s.toString().length() == 0) {
                        System.out.println("In if");
                        for (int i = 0; i < usernameIds.length; i++) {
                            if (displayingUsernameErrors[i]) {
                                TextView foundView = findViewById(usernameIds[i]);
                                ((ViewGroup) foundView.getParent()).removeView(foundView);
                                displayingUsernameErrors[i] = false;
                            }
                        }
                    }
                }
            }
        });
    }

}