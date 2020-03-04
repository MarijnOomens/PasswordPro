package com.example.passwordpro;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {
    private EditText passwordText;

    private Button confirmButton;
    private Button cancelButton;

    private MasterDatabaseHelper mDatabaseHelper;
    private DatabaseHelper standardDatabaseHelper;

    private MD5Hash md5class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        md5class = new MD5Hash();

        passwordText = findViewById(R.id.editText);

        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);

        mDatabaseHelper = new MasterDatabaseHelper(this);
        standardDatabaseHelper = new DatabaseHelper(this);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor pwsdata = mDatabaseHelper.getMasterPassword();

                String password = "";
                if(pwsdata.getCount() > 0) {
                    pwsdata.moveToPosition(pwsdata.getCount() - 1);
                    password = pwsdata.getString(0);
                }

                if(passwordText.toString() == "") {
                    toastMessage("Please enter a master password!");
                }

                else if(password != null) {
                    AlertDialog.Builder altDiag = new AlertDialog.Builder(RegisterActivity.this);
                    altDiag.setMessage("There is already an active account on this device! Do you want to delete the old account and all of it's passwords and create a new one?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createAccount();
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = altDiag.create();
                    alert.setTitle("Confirm overwrite");
                    alert.show();
                }

                else {
                    createAccount();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createAccount() {
        if(mDatabaseHelper.addPassword(md5class.md5(passwordText.getText().toString()))) {
            standardDatabaseHelper.deleteAll();
            toastMessage("Account created successfully");
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
