package com.example.passwordpro;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button loginbutton;
    private Button registerbutton;

    private EditText passwordText;

    private MasterDatabaseHelper mDatabaseHelper;

    private MD5Hash md5class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new MasterDatabaseHelper(this);

        md5class = new MD5Hash();

        loginbutton = findViewById(R.id.confirmButton);
        registerbutton = findViewById(R.id.cancelButton);
        passwordText = findViewById(R.id.editText);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor pwsdata = mDatabaseHelper.getMasterPassword();

                String password = "";
                if(pwsdata.getCount() > 0) {
                    pwsdata.moveToPosition(pwsdata.getCount() - 1);
                    password = pwsdata.getString(0);
                }

                if(passwordText.getText().toString().equals("")) {
                    toastMessage("Please enter a password!");
                }

                else if(!password.equals("")) {
                    if(md5class.md5(passwordText.getText().toString()).equals(password)) {
                        openMainMenu();
                    } else {
                        toastMessage("Wrong password!");
                    }
                } else {
                    toastMessage("No master password set!");
                }
            }
        });

        registerbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        TextView textView = findViewById(R.id.logotextView);

        String text = "PasswordPro";

        SpannableString ss = new SpannableString(text);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(Color.rgb(64, 129, 232));
        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.rgb(232, 64, 64));

        ss.setSpan(fcsBlue,0,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(fcsRed,8,11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);
    }

    public void openMainMenu() {
        Intent intent = new Intent(this, PasswordListMenuActivity.class);
        startActivity(intent);
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
