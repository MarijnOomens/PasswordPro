package com.example.passwordpro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPasswordActivity extends AppCompatActivity {

    private static final String TAG = "EditPasswordActivity";

    private PasswordGenerator generator;

    private EditText serviceText;
    private EditText passwordText;

    private Button generatePasswordbutton;
    private Button confirmButton;
    private Button deletebutton;

    DatabaseHelper mDatabaseHelper;

    private String selectedName;
    private String selectedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        //get the intent extra from the ListDataActivity
        Intent receivedIntent = getIntent();

        Bundle extras = receivedIntent.getExtras();

        //now get the name we passed as an extra
        selectedName = extras.getString("name");
        selectedPassword = extras.getString("password");

        serviceText = findViewById(R.id.service_edit_text);
        passwordText = findViewById(R.id.password_edit_text);

        serviceText.setText(selectedName);
        passwordText.setText(selectedPassword);

        mDatabaseHelper = new DatabaseHelper(this);

        generator = new PasswordGenerator();

        serviceText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    serviceText.setHint("");
                else
                    serviceText.setHint("Service name");
            }
        });

        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    passwordText.setHint("");
                else
                    passwordText.setHint("Password");
            }
        });

        generatePasswordbutton = findViewById(R.id.generatebutton);

        generatePasswordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordText.setText(generator.generatePassword());
            }
        });

        confirmButton = findViewById(R.id.confirmbutton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = serviceText.getText().toString();
                String password = passwordText.getText().toString();

                if (!(item.equals("") || password.equals(""))) {
                    try {
                        mDatabaseHelper.updateName(AESCrypt.encrypt(item), AESCrypt.encrypt(selectedName));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        mDatabaseHelper.updatePassword(AESCrypt.encrypt(password), AESCrypt.encrypt(item));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toastMessage("Change successful");
                    backToMenu();
                } else {
                    toastMessage("You must enter a name and a password!");
                }
            }
        });

        deletebutton = findViewById(R.id.deletebutton);

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mDatabaseHelper.deleteName(AESCrypt.encrypt(selectedName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                toastMessage("Password for " + selectedName + " removed");
                backToMenu();
            }
        });
    }

    private void backToMenu() {
        Intent intent = new Intent(this, PasswordListMenuActivity.class);
        try {
            Thread.sleep(500);
            startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
