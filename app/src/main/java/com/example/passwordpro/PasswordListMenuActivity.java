package com.example.passwordpro;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PasswordListMenuActivity extends AppCompatActivity {

    private static final String TAG = "PasswordListMenuActivity";

    private Button newPassword;

    private ListView serviceList;

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        serviceList = findViewById(R.id.listView);
        items = FileHelper.readData(this);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        serviceList.setAdapter(adapter);

        newPassword = findViewById(R.id.newPasswordButton);
        newPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPasswordCreation();
            }
        });

        mDatabaseHelper = new DatabaseHelper(this);

        populateListView();
    }

    private void openPasswordCreation() {
        Intent intent = new Intent(this, CreatePasswordActivity.class);
        startActivity(intent);
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();

        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            try {
                listData.add(AESCrypt.decrypt(data.getString(1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        serviceList.setAdapter(adapter);

        //set an onItemClickListener to the ListView
        serviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = "";
                try {
                    name = adapterView.getItemAtPosition(i).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Cursor data = mDatabaseHelper.getItemID(name); //get the id associated with that name
                Cursor pswdata = null;
                try {
                    pswdata = mDatabaseHelper.getItemPassword(AESCrypt.encrypt(name));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //int itemID = data.getInt(0);
                String password = "";
                if(pswdata.getCount() > 0) {
                    pswdata.moveToPosition(pswdata.getCount() - 1);
                    try {
                        password = AESCrypt.decrypt(pswdata.getString(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //String password = pswdata.getString(0);

//                while(data.moveToNext()){
//                    itemID = data.getInt(0);
//                }
                //if(itemID > -1){
                   // Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent editScreenIntent = new Intent(PasswordListMenuActivity.this, EditPasswordActivity.class);
                    Bundle extra = new Bundle();
                    //extra.putInt("id",itemID);
                    extra.putString("name",name);
                    extra.putString("password",password);

                    editScreenIntent.putExtras(extra);

                    startActivity(editScreenIntent);
                //}
//                else{
//                    toastMessage("No ID associated with that name");
//                }
            }
        });


    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
