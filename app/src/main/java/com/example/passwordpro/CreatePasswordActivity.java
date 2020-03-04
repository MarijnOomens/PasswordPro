package com.example.passwordpro;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreatePasswordActivity";

    private PasswordGenerator generator;

    private EditText serviceText;
    private EditText passwordText;

    private Button generatePasswordbutton;
    private Button addbutton;

    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        mDatabaseHelper = new DatabaseHelper(this);

        generator = new PasswordGenerator();

        serviceText = findViewById(R.id.service_edit_text);

        serviceText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    serviceText.setHint("");
                else
                    serviceText.setHint("Service name");
            }
        });

        passwordText = findViewById(R.id.password_edit_text);

        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    passwordText.setHint("");
                else
                    passwordText.setHint("Password");
            }
        });

        generatePasswordbutton = findViewById(R.id.generatebutton);
        addbutton = findViewById(R.id.addbutton);

        generatePasswordbutton.setOnClickListener(this);
        addbutton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.generatebutton) {
                passwordText.setText(generator.generatePassword());
        }
        else if (v.getId() == R.id.addbutton) {
            String newEntry = serviceText.getText().toString();
            String newEntry2 = passwordText.getText().toString();

            Cursor pswdata = null;
            try {
                pswdata = mDatabaseHelper.getItemPassword(AESCrypt.encrypt(newEntry));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(pswdata.getCount() <= 0) {
                if (newEntry.length() != 0 && newEntry2.length() != 0) {
                    AddData(newEntry,newEntry2);
                    serviceText.setText("");
                    passwordText.setText("");
                    serviceText.clearFocus();
                    passwordText.clearFocus();
                } else {
                    toastMessage("You must put something in the text fields!");
                }
            } else {
                toastMessage("A service with this name already exists, please use a different name!");
            }
        }
    }

    public void AddData(String serviceEntry, String passwordEntry) {
        boolean insertData = false;
        try {
            insertData = mDatabaseHelper.addData(AESCrypt.encrypt(serviceEntry), AESCrypt.encrypt(passwordEntry));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (insertData) {
            toastMessage("Password successfully added!");
            backToMenu();

        } else {
            toastMessage("Something went wrong...");
        }
    }

    private void backToMenu() {
        Intent intent = new Intent(this, PasswordListMenuActivity.class);
        try {
            Thread.sleep(200);
            startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
