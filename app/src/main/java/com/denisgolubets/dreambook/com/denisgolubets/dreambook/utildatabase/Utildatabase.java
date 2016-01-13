package com.denisgolubets.dreambook.com.denisgolubets.dreambook.utildatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.denisgolubets.dreambook.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Utildatabase extends SQLiteOpenHelper {

    private final String TAG = "utildatabase";

    private static String DB_PATH = "/data/data/com.denisgolubets.dreambook/databases/";

    private static final String DATABASE_NAME = "drm";

    private static final int DATABASE_VERSION = 1;
    private final Context myContext;
    public SQLiteDatabase myDataBase = null;

    private static final String DATABASE_TABLE_MAIN = "main";
    private static final String DATABASE_TABLE_MAIN_NAME_COLUMN_ = "name";
    private static final String DATABASE_TABLE_MAIN_DESCRIPTION_COLUMN_ = "description";
    private static final String DATABASE_TABLE_MAIN_CATEGORY_COLUMN_ = "category";
    private static final String DATABASE_TABLE_MAIN_SUBCATEGORY_COLUMN_ = "subcategory";

    private static final String DATABASE_TABLE_CATEGORY = "category";
    private static final String DATABASE_TABLE_CATEGORY_CATEGORYNAME_COLUMN = "categoryname";

    private static final String DATABASE_TABLE_SUBCATEGORY = "subcategory";
    private static final String DATABASE_TABLE_SUBCATEGORY_SUBCOTEGORYNAME_COLUMN = "subcategoryname";


    //конструкторы
    public Utildatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void createDB() {
        if (checkDataBase()) {
            try {
                copyDataBaseFromZipFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                openDataBase();
            } catch (SQLException e) {
                e.printStackTrace();
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

    // копирование базы из zip
    private void copyDataBaseFromZipFile() throws IOException {
        InputStream is = myContext.getResources().openRawResource(R.raw.drm);
        File outFile = new File(DB_PATH, DATABASE_NAME);
        OutputStream myOutput = new FileOutputStream(outFile.getAbsolutePath());
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        try {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;

                while ((count = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                }
                baos.writeTo(myOutput);
                Log.d(TAG, "copyDataBaseFromZipFile - OK");

            }
        } finally {
            zis.close();
            myOutput.flush();
            myOutput.close();
            is.close();
        }
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