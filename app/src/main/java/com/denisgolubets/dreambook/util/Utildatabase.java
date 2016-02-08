package com.denisgolubets.dreambook.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.denisgolubets.dreambook.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Utildatabase extends SQLiteOpenHelper {

    private final String TAG = "utildatabase";

    private static String DB_PATH = "/data/data/com.denisgolubets.dreambook/databases/";

    private static final String DATABASE_NAME = "drm";

    private static final int DATABASE_VERSION = 2;
    private final Context myContext;
    public SQLiteDatabase myDataBase = null;

    private static final String DATABASE_TABLE_MAIN = "main";
    private static final String DATABASE_TABLE_MAIN_NAME_COLUMN_ = "name";
    private static final String DATABASE_TABLE_MAIN_DESCRIPTION_COLUMN_ = "description";

    private static final String[] tables = new String[]{"english", "assyrian", "old_russian",
            "indian", "culinary", "love", "muslim", "persian", "right", "slavic", "azar", "vanga",
            "kopalinski", "krada_velez", "lof", "meneghetti", "miller", "nostradamus", "solomon", "freid",
            "tsvetkov", "hasse", "zhou_goun", "yuri_long", "ukraine", "french", "esoteric", "electronic"};


    //конструкторы
    public Utildatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }


    public Cursor firstQuery(String s) throws SQLException {
        Cursor cursorFirstQuery = null;
        openDataBase();
        String query = "";
        for (int i = 0; i < tables.length; i++) {
            query += "SELECT _id,dream_name,file_description FROM " + tables[i] + " where dream_name Like \"" + s + "%\"";
            if (i < tables.length - 1) {
                query += " UNION ";
            }

        }

        cursorFirstQuery = myDataBase.rawQuery(query, null);
        return cursorFirstQuery;
    }

    public ArrayList<String> resultWords(String s) {
        ArrayList<String> wordList = new ArrayList<String>();
        try {
            openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // String query = "SELECT dream_name FROM miller  UNION SELECT dream_name FROM meneghetti UNION SELECT dream_name FROM vanga UNION SELECT dream_name FROM freid UNION SELECT dream_name FROM solomon UNION SELECT dream_name FROM tsvetkov UNION SELECT dream_name FROM nostradamus UNION SELECT dream_name FROM hasse UNION SELECT dream_name FROM azar UNION SELECT dream_name FROM krada_velez UNION SELECT dream_name FROM kopalinski UNION SELECT dream_name FROM zhou_goun UNION SELECT dream_name FROM yuri_long UNION SELECT dream_name FROM english UNION SELECT dream_name FROM assyrian UNION SELECT dream_name FROM old_russian UNION SELECT dream_name FROM indian UNION SELECT dream_name FROM culinary UNION SELECT dream_name FROM lof UNION SELECT dream_name FROM love UNION SELECT dream_name FROM muslim UNION SELECT dream_name FROM persian UNION SELECT dream_name FROM slavic UNION SELECT dream_name FROM ukraine UNION SELECT dream_name FROM french UNION SELECT dream_name FROM esoteric UNION SELECT dream_name FROM electronic UNION SELECT dream_name FROM right";
        String query = "";
        for (int i = 0; i < tables.length; i++) {
            query += "SELECT dream_name FROM " + tables[i] + " where dream_name Like \"" + s + "%\"";
            if (i < tables.length - 1) {
                query += " UNION ";
            }

        }
        Cursor cursor = myDataBase.rawQuery(query, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount()>0){
            do {
                wordList.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }


        return wordList;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void createDB() {//if file exist copy DB from file
        if (checkDataBase()) {
            try {
                onUnzipZip();
                Log.d(TAG,"database unziped");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {//if file exist try open DB
            try {
                openDataBase();//if catch Exception try copy DB from file
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    copyDataBase();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (DATABASE_VERSION > getVersionId()) {
                deleteDB();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void deleteDB() {
        File file = new File(DB_PATH + DATABASE_NAME);
        if (file.exists())
            file.delete();
        Log.d(TAG, "Database deleted.");

    }

    public int getVersionId() {
        int v = DATABASE_VERSION;

        try {
            // String DB_FULL_PATH = DB_PATH + DATABASE_NAME;
            //openDataBase();
            Cursor cursor = myDataBase.rawQuery("SELECT version_id FROM dbVersion", null);
            cursor.moveToLast();
            v = cursor.getInt(0);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return v;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //Проверка соществования базы данных.
    private boolean checkDataBase() {
        boolean checkDB = false;

        try {
            String myPath = DB_PATH + DATABASE_NAME;

            File chelDBfile = new File(myPath);
            checkDB = chelDBfile.exists();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            //база не существует.

        }

        return !checkDB;
    }

    //копирование базы из папки assets
    private void copyDataBase() throws IOException {
        InputStream is;
        OutputStream os;
        try {
            File file = new File(DB_PATH + DATABASE_NAME);
            if (!file.exists()) {
                this.getReadableDatabase();
                is = myContext.getAssets().open(DATABASE_NAME);
                String outFileName = DB_PATH + DATABASE_NAME;
                os = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        Log.d(TAG, "copyDataBase - OK");
    }


    // крпипрваеие база из архива
    public void onUnzipZip() throws IOException {
        InputStream is = myContext.getAssets().open("data.zip");
        File db_path = myContext.getDatabasePath(DB_PATH + DATABASE_NAME);
        if (!db_path.exists())
            db_path.getParentFile().mkdirs();
        OutputStream os = new FileOutputStream(db_path);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            byte[] buffer = new byte[1024];
            int count;
            while ((count = zis.read(buffer)) > -1) {
                os.write(buffer, 0, count);
            }
            os.close();
            zis.closeEntry();
        }
        zis.close();
        is.close();

    }


    //открываем базу
    public void openDataBase() throws SQLException {

        if (myDataBase == null) {
            String myPath = DB_PATH + DATABASE_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            Log.d(TAG, "DataBase is opened");
        }
    }

    //закрываем
    @Override
    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
            myDataBase = null;
            Log.d(TAG, "DataBase is closet");
        }
        super.close();

    }


}