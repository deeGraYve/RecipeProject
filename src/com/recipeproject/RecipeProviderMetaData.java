package com.recipeproject;

import android.net.Uri;
import android.provider.BaseColumns;

public class RecipeProviderMetaData
{
	public static final String AUTHORITY = "com.androidbook.provider.RecipeProvider";
	
	public static final String DATABASE_NAME = "recipes.db";
	public static final int DATABASE_VERSION = 2;
	public static final String RECIPES_TABLE_NAME = "recipes";
    
    private RecipeProviderMetaData() {}
    
    public static class RecipeTableMetaData implements BaseColumns
    {
    	private RecipeTableMetaData() {}
    	public static final String TABLE_NAME = "recipes";
    	
    	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/recipes");
    	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android.recipe";
    	public static final String CONTENT_ITEM_TYPE = "vnc.android.cursor.item/vnd.android.recipe";
    	public static final String DEFAULT_SORT_ORDER = "modified DESC";
    	
    	public static final String RECIPE_NAME = "name";
    }
}
