package com.recipeproject;

import java.util.HashMap;

import com.recipeproject.RecipeProviderMetaData;
import com.recipeproject.RecipeProviderMetaData.RecipeTableMetaData;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class RecipeProvider extends ContentProvider {

	private static final String TAG = "RecipeProvider";
    
    private static HashMap<String, String> sRecipesProjectionMap;
    static
    {
    	sRecipesProjectionMap = new HashMap<String, String>();
    	sRecipesProjectionMap.put(RecipeTableMetaData._ID, RecipeTableMetaData._ID);
    	sRecipesProjectionMap.put(RecipeTableMetaData.RECIPE_NAME, RecipeTableMetaData.RECIPE_NAME);
    }

    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_RECIPE_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_RECIPE_URI_INDICATOR = 2;
    static
    {
    	sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    	sUriMatcher.addURI(RecipeProviderMetaData.AUTHORITY, "recipes", INCOMING_RECIPE_COLLECTION_URI_INDICATOR);
    	sUriMatcher.addURI(RecipeProviderMetaData.AUTHORITY, "recipes/#", INCOMING_SINGLE_RECIPE_URI_INDICATOR);
    }
	
    private DatabaseHelper mOpenHelper;
    
    @Override
    public boolean onCreate() {
    	mOpenHelper = new DatabaseHelper(getContext());
    	return true;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, RecipeProviderMetaData.DATABASE_NAME, null, RecipeProviderMetaData.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + RECIPES_TABLE_NAME + " ("
                    + Recipes._ID + " INTEGER PRIMARY KEY,"
                    + Recipes.NAME + " TEXT,"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
