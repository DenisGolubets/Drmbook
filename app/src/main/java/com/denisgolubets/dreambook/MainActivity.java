package com.denisgolubets.dreambook;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.denisgolubets.dreambook.util.Utildatabase;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, TextWatcher, View.OnClickListener {
    private Utildatabase sqlHelper;
    private ListView lv;
    private EditText etname;
    private ArrayList<String> words;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private Button btnSerch, btnClear;
    private Button Clear;
    private LinearLayout mLinearLayout;


    private AutoCompleteTextView edit;

    private Utildatabase mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edit = (AutoCompleteTextView) findViewById(R.id.edit);
        edit.setOnEditorActionListener(this);
        edit.addTextChangedListener(this);


        btnSerch = (Button) findViewById(R.id.btnSearch);
        btnSerch.setOnClickListener(this);


        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);
        btnClear.setVisibility(View.INVISIBLE);

        sqlHelper = new Utildatabase(this);
        sqlHelper.createDB();

        lv = (ListView) findViewById(R.id.lv);


        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_focus);
        // edit.requestFocus();

        /*try {
            sqlHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/


        // edit.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,words));


    }

    @Override
    protected void onResume() {
        super.onResume();


        edit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    cursor = sqlHelper.firstQuery(edit.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                setList();
                hideKeyboard();

            }
        });

    }

    private void setList() {
        String[] headers = new String[]{"dream_name", "file_description"};
        adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                cursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    public void onDestroy() {
        super.onDestroy();
        sqlHelper.close();
//        cursor.close();

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (edit.length() > 1) {
            btnClear.setVisibility(View.VISIBLE);

        }
        if (edit.length() < 2) {
            btnClear.setVisibility(View.INVISIBLE);
        }
        if (edit.getText().length() > 0) {
            String editString = edit.getText().toString();
            if (editString.length() == 1) {
                editString.toUpperCase();
            } else {
                editString = Character.toString(editString.charAt(0)).toUpperCase() + editString.substring(1);
            }
            words = sqlHelper.resultWords(editString);
            edit.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, words));

        }


    }


    public void afterTextChanged(Editable s) {

    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch: {
                sershClick();

            }
            break;
            case R.id.btnClear: {
                edit.setText("");

                edit.requestFocus();

            }
            break;


        }

    }

    private void sershClick() {
        if (edit.getText().length() > 0) {
            String editString = edit.getText().toString();
            if (editString.length() == 1) {
                editString.toUpperCase();
            } else {
                editString = Character.toString(editString.charAt(0)).toUpperCase() + editString.substring(1);
            }
            try {

                cursor = sqlHelper.firstQuery(editString);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String i = String.valueOf(cursor.getCount());
            Toast.makeText(this, i, Toast.LENGTH_SHORT).show();
            setList();
        }
        hideKeyboard();
        edit.clearFocus();
        mLinearLayout.requestFocus();
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            sershClick();
            return true;
        }
        return false;
    }
}
