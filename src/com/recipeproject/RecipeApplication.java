package com.recipeproject;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;

public class RecipeApplication extends Application{

    public static RecipeDBHelper dbHelper;
    public static Context Context;

    public void onCreate() {

        super.onCreate();

        RecipeApplication.Context = this.getApplicationContext();

        String[] projection = new String[]{
                RecipeProviderMetaData.RecipeTableMetaData._ID,
                RecipeProviderMetaData.RecipeTableMetaData.RECIPE_NAME
        };

        dbHelper = RecipeDBHelper.getInstance();

        Cursor cursor = getContentResolver().query(RecipeProviderMetaData.RecipeTableMetaData.CONTENT_URI, projection, null, null, null);

    }
}
