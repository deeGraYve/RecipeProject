package com.recipeproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public class RecipeDBHelper{

    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 2;
    private static RecipeDBHelper instance;

    public static final HashMap<String, String> COLUMNS = new HashMap<String, String>() {
        private static final long serialVersionUID = 968037281182433351L;

        {
            put("a", "int");
            put("b", "TIMESTAMP");
            put("c", "string");
        }
    };

    private static HashMap<String, HashMap<String, String>> TABLE_COLUMNS = new HashMap<String, HashMap<String, String>>() {
        private static final long serialVersionUID = -4377126337880545188L;

        {
            put("TEST_TABLE", COLUMNS);
        }
    };

    private Context context;
    private SQLiteDatabase db;

    private RecipeDBHelper(Context context) {
        this.context = context;
        OpenHelper openHelper = new OpenHelper(this.context);
        this.db = openHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public static RecipeDBHelper getInstance() {
        if (null == instance) {
            instance = new RecipeDBHelper(RecipeApplication.Context);
        }
        return instance;
    }

    private static class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (String TABLE_NAME : TABLE_COLUMNS.keySet()) {
                String query = "CREATE TABLE " + TABLE_NAME
                        + "(id INTEGER PRIMARY KEY AUTOINCREMENT";
                HashMap<String, String> map = TABLE_COLUMNS.get(TABLE_NAME);
                for (Map.Entry<String, String> pair : map.entrySet()) {
                    query += ", " + pair.getKey() + " " + pair.getValue();
                }
                query += ")";
                db.execSQL(query);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (String TABLE_NAME : TABLE_COLUMNS.keySet()) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            }
            onCreate(db);
        }
    }
}
