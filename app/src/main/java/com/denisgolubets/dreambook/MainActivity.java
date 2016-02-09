package com.denisgolubets.dreambook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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

import com.denisgolubets.dreambook.util.HomeItem;
import com.denisgolubets.dreambook.util.HomeListAdapter;
import com.denisgolubets.dreambook.util.SeparatedListAdapter;
import com.denisgolubets.dreambook.util.Utildatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener, TextWatcher, View.OnClickListener {
    private static final String MY_SETTINGS = "drm_preferences";
    private Utildatabase sqlHelper;
    private ListView lv;
    private EditText etname;
    private ArrayList<String> words;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private Button btnSerch, btnClear;
    private Button Clear;
    private LinearLayout mLinearLayout;

    private HomeListAdapter HomeListAdapter;
    private SeparatedListAdapter sadapter;
    private ArrayList<HomeItem> HomeItemList;

    private static final String[] tables = new String[]{"english", "assyrian", "old_russian",
            "indian", "culinary", "love", "muslim", "persian", "right", "slavic", "azar", "vanga",
            "kopalinski", "krada_velez", "lof", "meneghetti", "miller", "nostradamus", "solomon", "freid",
            "tsvetkov", "hasse", "zhou_goun", "yuri_long", "ukraine", "french", "esoteric", "electronic"};


    public HashMap<String, Boolean> tablesSearch = new HashMap<String, Boolean>();

    private static final String[] headers = new String[]{"Английский сонник", "Ассирийский сонник",
            "Древнерусский сонник", "Индийский сонник", "Кулинарный сонник", "Любовный сонник", "Мусульманский сонник",
            "Персидский сонник", "Правильный сонник", "Славянский сонник", "Сонник Азара", "Сонник Ванги",
            "Сонник Копалинского", "Сонник Крады Велес", "Сонник Лоффа", "Cонник Менегетти", "Сонник Миллера",
            "Сонник Нострадамуса", "Cонник Соломона", "Сонник Фрейда", "Сонник Цветкова", "Сонник Хассе", "Cонник Чжоу-Гуна",
            "Cонник Юрия Лонго", "Украинский сонник", "Французский сонник", "Эзотерический сонник", "Электронный сонник"};


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


    }

    private void loadPreferences() {
        SharedPreferences sp = getSharedPreferences(getPackageName() + "_preferences",
                Context.MODE_PRIVATE);
        boolean hasVisited = sp.getBoolean("hasVisited", false);
        if (!hasVisited) {
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.putBoolean("muslim", true);
            e.putBoolean("old_russian", true);
            e.putBoolean("persian", true);
            e.putBoolean("kopalinski", true);
            e.putBoolean("french", true);
            e.putBoolean("esoteric", true);
            e.putBoolean("meneghetti", true);
            e.putBoolean("assyrian", true);
            e.putBoolean("love", true);
            e.putBoolean("krada_velez", true);
            e.putBoolean("right", true);
            e.putBoolean("miller", true);
            e.putBoolean("tsvetkov", true);
            e.putBoolean("nostradamus", true);
            e.putBoolean("azar", true);
            e.putBoolean("solomon", true);
            e.putBoolean("indian", true);
            e.putBoolean("lof", true);
            e.putBoolean("culinary", true);
            e.putBoolean("electronic", true);
            e.putBoolean("yuri_long", true);
            e.putBoolean("slavic", true);
            e.putBoolean("zhou_goun", true);
            e.putBoolean("ukraine", true);
            e.putBoolean("hasse", true);
            e.putBoolean("vanga", true);
            e.putBoolean("english", true);
            e.commit();
        } else {
            for (int i = 0; i < tables.length; i++) {
                tablesSearch.put(tables[i], sp.getBoolean(tables[i], true));
            }
        }

    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, Prefs.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();


        edit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchClick();


            }
        });

    }

    private void setListCursorAdapter() {
        String[] headers = new String[]{"dream_name", "file_description"};
        adapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                cursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        lv.setAdapter(adapter);
    }

    private void setListWithHeader() {
        sadapter = new SeparatedListAdapter(this);
        String editString = setTextToUpperCase();

        if (numbersOfBase() == 0) {
            Toast.makeText(this, "Добавьте в настройках хотябы одну базу", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < tables.length; i++) {
                if (checkTableForSearch(tables[i])) {

                    try {

                        cursor = sqlHelper.secondQuery(tables[i], editString);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    HomeItemList = new ArrayList<HomeItem>();

                    if (cursor.getCount() > 0) {
                        for (int j = 0; j < cursor.getCount(); j++) {
                            HomeItem homeItem = new HomeItem();
                            homeItem.setHomeItemID(j);
                            homeItem.setHomeItemName(cursor.getString(1));
                            homeItem.setHomeItemDescription(cursor.getString(2));
                            HomeItemList.add(homeItem);
                            if (cursor.getCount()>1){
                                cursor.moveToNext();
                            }

                        }
                        HomeListAdapter = new HomeListAdapter(getApplicationContext(), 0, HomeItemList);
                        //if (HomeListAdapter != null)
                            sadapter.addSection(headers[i], HomeListAdapter);

                    }
                }
                cursor=null;
            }
            if (sadapter.getCount() == 0) {
                Toast.makeText(this, "По вашему запросу ничего не найдено", Toast.LENGTH_SHORT).show();
            }


        }


        lv.setAdapter(sadapter);
        Log.d("lv.setAdapter(sadapter)", "set");
        if (cursor != null) cursor.close();


    }

    private int numbersOfBase() {
        int count = 0;
        for (int i = 0; i < tables.length; i++) {
            if (tablesSearch.get(tables[i])) {
                count++;
            }
        }
        return count;

    }


    public boolean checkTableForSearch(String table) {
        return (tablesSearch.get(table)) ? true : false;
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
        String editString;
        if (edit.length() > 1) {
            btnClear.setVisibility(View.VISIBLE);

        }
        if (edit.length() < 2) {
            btnClear.setVisibility(View.INVISIBLE);
        }
        if (edit.getText().length() > 0) {

            editString = setTextToUpperCase();
            words = sqlHelper.resultWords(editString, tablesSearch);
            edit.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, words));

        }


    }

    private String setTextToUpperCase() {
        String editString;
        if (edit.getText().length() == 1) {
            editString = edit.getText().toString().toUpperCase();
        } else {
            editString = Character.toString(edit.getText().toString().charAt(0)).toUpperCase() + edit.getText().toString().substring(1);
        }
        return editString;
    }


    public void afterTextChanged(Editable s) {

    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch: {
                searchClick();

            }
            break;
            case R.id.btnClear: {
                clearEditAndLv();

            }
            break;


        }

    }

    private void clearEditAndLv() {
        edit.setText("");
        adapter = null;
        lv.setAdapter(adapter);
        edit.requestFocus();
        showKeyboard();
    }

    private void searchClick() {
        if (edit.getText().length() > 0) {


            setListWithHeader();
        }
        hideKeyboard();
        edit.clearFocus();
        mLinearLayout.requestFocus();
    }

    private void searchClick_OLD() {
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
            //setListCursorAdapter();
            setListWithHeader();
        }
        hideKeyboard();
        edit.clearFocus();
        mLinearLayout.requestFocus();
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            searchClick();
            return true;
        }
        return false;
    }
}
