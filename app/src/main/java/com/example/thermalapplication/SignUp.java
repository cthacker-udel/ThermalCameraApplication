package com.example.thermalapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import helpers.RegularExpressions;
import repository.firestore.datamodel.User;
import repository.firestore.manager.UserFirestoreManager;

class FormState {
    private boolean usernameValid;
    private boolean passwordValid;
    private boolean confirmPasswordValid;

    public FormState() {
        this.usernameValid = false;
        this.passwordValid = false;
        this.confirmPasswordValid = false;
    }

    public FormState(boolean usernameValid, boolean passwordValid, boolean confirmPasswordValid) {
        this.usernameValid = usernameValid;
        this.passwordValid = passwordValid;
        this.confirmPasswordValid = confirmPasswordValid;
    }

    public boolean isUsernameValid() {
        return usernameValid;
    }

    public FormState setUsernameValid(boolean usernameValid) {
        this.usernameValid = usernameValid;
        return this;
    }

    public boolean isPasswordValid() {
        return passwordValid;
    }

    public FormState setPasswordValid(boolean passwordValid) {
        this.passwordValid = passwordValid;
        return this;
    }

    public boolean isConfirmPasswordValid() {
        return confirmPasswordValid;
    }

    public FormState setConfirmPasswordValid(boolean confirmPasswordValid) {
        this.confirmPasswordValid = confirmPasswordValid;
        return this;
    }

    public boolean isFormValid() {
        return this.passwordValid && this.confirmPasswordValid && this.usernameValid;
    }
}

public class SignUp extends AppCompatActivity {

    public static String[] UsernameValidationMessages = new String[] { "\u2022 Username must be more then 7 characters",  "\u2022 Username must contain at least 1 digit", "\u2022 Username must contain at least one symbol", "\u2022 Username must contain at least one uppercase character", "\u2022 Username must contain at least one lowercase character",};
    public static String[] PasswordValidationMessages = new String[] { "\u2022 Password must be at least 7 characters", "\u2022 Password must contain at least 1 digit", "\u2022 Password must contain at least one symbol", "\u2022 Password must contain at least one uppercase character", "\u2022 Password must contain at least one lowercase character",};
    public static String[] ConfirmPasswordValidationMessages = new String[] { "\u2022 Passwords must match"};

    private EditText username;
    private EditText password;
    private EditText confirmPassword;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText phoneNumber;
    private MutableLiveData<FormState> formState;
    private Button signUpButton;
    private UserFirestoreManager userFirestoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.username = findViewById(R.id.signUpUsernameInput);
        this.password = findViewById(R.id.signUpPasswordInput);
        this.confirmPassword = findViewById(R.id.signUpConfirmPasswordInput);
        this.firstName = findViewById(R.id.signUpFirstNameInput);
        this.lastName = findViewById(R.id.signUpLastNameInput);
        this.email = findViewById(R.id.signUpEmailInput);
        this.phoneNumber = findViewById(R.id.phoneNumberInput);
        this.signUpButton = findViewById(R.id.signUpButton);
        this.formState = new MutableLiveData<>();
        this.formState.setValue(new FormState());
        this.userFirestoreManager = new UserFirestoreManager(FirebaseFirestore.getInstance());

        this.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean[] passwordValidationErrors = RegularExpressions.ValidatePassword(s.toString());
                StringBuilder passwordErrors = new StringBuilder();
                for (int i = 0; i < passwordValidationErrors.length; i++) {
                    if (passwordValidationErrors[i]) {
                        passwordErrors.append(String.format("%s\n", PasswordValidationMessages[i]));
                    }
                }
                if (passwordErrors.length() > 0) {
                    password.setError(passwordErrors.toString());
                    formState.setValue(Objects.requireNonNull(formState.getValue()).setPasswordValid(false));
                } else {
                    password.setError(null);
                    formState.setValue(Objects.requireNonNull(formState.getValue()).setPasswordValid(true));
                }
            }
        });

        this.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean[] usernameValidation = RegularExpressions.ValidateUsername(s.toString());
                StringBuilder usernameErrors = new StringBuilder();
                for (int i = 0; i < usernameValidation.length; i++) {
                    if (usernameValidation[i]) {
                        usernameErrors.append(String.format("%s\n", UsernameValidationMessages[i]));
                    }
                }
                if (usernameErrors.length() > 0) {
                    username.setError(usernameErrors.toString());
                    formState.setValue(Objects.requireNonNull(formState.getValue()).setUsernameValid(false));
                } else {
                    username.setError(null);
                    formState.setValue(Objects.requireNonNull(formState.getValue()).setUsernameValid(true));
                }
            }
        });

        this.confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String passwordValue = password.getText().toString();
                if (!s.toString().equals(passwordValue)) {
                    confirmPassword.setError(ConfirmPasswordValidationMessages[0]);
                    formState.setValue(Objects.requireNonNull(formState.getValue()).setConfirmPasswordValid(false));
                } else {
                    formState.setValue(Objects.requireNonNull(formState.getValue()).setConfirmPasswordValid(true));
                    confirmPassword.setError(null);
                }
            }
        });


        this.formState.observe(this, new Observer<FormState>() {
            @Override
            public void onChanged(FormState formState) {
                signUpButton.setEnabled(formState.isFormValid());
            }
        });

        this.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User newUser = new User();
                newUser.setPassword(password.getText().toString()).setUsername(username.getText().toString()).setEmail(email.getText().toString());
            }
        });
    }
}