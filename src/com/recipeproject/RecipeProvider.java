package com.recipeproject;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.recipeproject.RecipeProviderMetaData.RecipeTableMetaData;

public class RecipeProvider extends ContentProvider {

	private static final String TAG = "RecipeProvider";
    
    private static HashMap<String, String> sRecipesProjectionMap;
    static
    {
    	sRecipesProjectionMap = new HashMap<String, String>();
    	sRecipesProjectionMap.put(RecipeTableMetaData._ID, RecipeTableMetaData._ID);
    	sRecipesProjectionMap.put(RecipeTableMetaData.RECIPE_NAME, RecipeTableMetaData.RECIPE_NAME);
    	sRecipesProjectionMap.put(RecipeTableMetaData.CREATED_DATE, RecipeTableMetaData.CREATED_DATE);
    	sRecipesProjectionMap.put(RecipeTableMetaData.MODIFIED_DATE, RecipeTableMetaData.MODIFIED_DATE);
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
            db.execSQL("CREATE TABLE " + RecipeProviderMetaData.RECIPES_TABLE_NAME + " ("
                    + RecipeProviderMetaData.RecipeTableMetaData._ID + " INTEGER PRIMARY KEY,"
                    + RecipeProviderMetaData.RecipeTableMetaData.RECIPE_NAME + " TEXT,"
                    + RecipeProviderMetaData.RecipeTableMetaData.CREATED_DATE + " INTEGER,"
                    + RecipeProviderMetaData.RecipeTableMetaData.MODIFIED_DATE + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS" + RecipeProviderMetaData.RECIPES_TABLE_NAME);
            onCreate(db);
        }
    }
    
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch(sUriMatcher.match(uri)) {
		case INCOMING_RECIPE_COLLECTION_URI_INDICATOR:
			count = db.delete(RecipeTableMetaData.TABLE_NAME, where, whereArgs);
			break;
			
		case INCOMING_SINGLE_RECIPE_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = db.delete(RecipeTableMetaData.TABLE_NAME, 
					RecipeTableMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(where) ? "AND (" + where + ")" : ""), 
					whereArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch(sUriMatcher.match(uri)) {
		case INCOMING_RECIPE_COLLECTION_URI_INDICATOR:
			return RecipeTableMetaData.CONTENT_TYPE;
		case INCOMING_SINGLE_RECIPE_URI_INDICATOR:
			return RecipeTableMetaData.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//Validate the requested uri
		if (sUriMatcher.match(uri) != INCOMING_RECIPE_COLLECTION_URI_INDICATOR) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		Long now = Long.valueOf(System.currentTimeMillis());
		
		//validate input fields
		//Make sure that the fields are all set
		if (values.containsKey(RecipeTableMetaData.CREATED_DATE) == false) {
			values.put(RecipeTableMetaData.CREATED_DATE, now);
		}
		
		if (values.containsKey(RecipeTableMetaData.MODIFIED_DATE) == false) {
			values.put(RecipeTableMetaData.MODIFIED_DATE, now);
		}
		
		if (values.containsKey(RecipeTableMetaData.RECIPE_NAME) == false) {
			throw new SQLException("Failed to insert row because Recipe Name is needed " + uri);
		}
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(RecipeTableMetaData.TABLE_NAME, RecipeTableMetaData.RECIPE_NAME, values);
		
		if (rowId > 0) {
			Uri insertedRecipeUri = ContentUris.withAppendedId(RecipeTableMetaData.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(insertedRecipeUri, null);
			return insertedRecipeUri;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch(sUriMatcher.match(uri)) {
		case INCOMING_RECIPE_COLLECTION_URI_INDICATOR:
			qb.setTables(RecipeTableMetaData.TABLE_NAME);
			qb.setProjectionMap(sRecipesProjectionMap);
			break;
		
		case INCOMING_SINGLE_RECIPE_URI_INDICATOR:
			qb.setTables(RecipeTableMetaData.TABLE_NAME);
			qb.setProjectionMap(sRecipesProjectionMap);
			qb.appendWhere(RecipeTableMetaData._ID + "=" + uri.getPathSegments().get(1));
			break;
		
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = RecipeTableMetaData.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		
		//Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		//int i = c.getCount();
		
		//Tell the cursor what uri to watch , so it knows when its source data changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch(sUriMatcher.match(uri)) {
		case INCOMING_RECIPE_COLLECTION_URI_INDICATOR:
			count = db.update(RecipeTableMetaData.TABLE_NAME, values, where, whereArgs);
			break;
		
		case INCOMING_SINGLE_RECIPE_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = db.update(RecipeTableMetaData.TABLE_NAME, 
					values, 
					RecipeTableMetaData._ID + "=" + rowId + (!TextUtils.isEmpty(where) ? "AND (" + where + ")" : ""), 
					whereArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);		
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
