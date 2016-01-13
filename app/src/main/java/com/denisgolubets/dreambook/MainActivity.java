package com.denisgolubets.dreambook;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.denisgolubets.dreambook.com.denisgolubets.dreambook.utildatabase.Utildatabase;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    Utildatabase sqlHelper;

    TextView tv1;

    private Utildatabase mDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlHelper = new Utildatabase(this);
        sqlHelper.createDB();
        try {
            sqlHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {

        super.onResume();
        int v=sqlHelper.getVersionId();

        tv1 = (TextView)findViewById(R.id.tv1);



        tv1.setText(String.valueOf(v));
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlHelper.close();
                sqlHelper.deleteDB();
            }
        });


    }



    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sqlHelper.close();

    }

}
