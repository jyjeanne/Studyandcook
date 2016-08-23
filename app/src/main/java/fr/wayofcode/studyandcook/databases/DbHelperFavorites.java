package fr.wayofcode.studyandcook.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import fr.wayofcode.studyandcook.utils.Utils;

/**
 * Created by jeremy on 22/05/2016.
 */
public class DbHelperFavorites extends SQLiteOpenHelper {
    // Path of database when app installed on device
    private static String DB_PATH = Utils.ARG_DATABASE_PATH;

    // Create database name and version
    private final static String DB_NAME = "db_favorites";
    public final static int DB_VERSION = 1;
    public static SQLiteDatabase db;

    private final Context context;

    // Create table name and fields
    private final static String TABLE_RECIPES = "tbl_recipes";
    private final static String RECIPE_ID = "recipe_id";
    private final static String CATEGORY_ID = "category_id";
    private final static String RECIPE_NAME = "recipe_name";
    private final static String RECIPE_PRICE = "recipe_price";
    private final static String COOK_TIME = "cook_time";
    private final static String SERVINGS = "servings";
    private final static String SUMMARY = "summary";
    private final static String INGREDIENTS = "ingredients";
    private final static String STEPS = "steps";
    private final static String IMAGE = "image";


    public DbHelperFavorites(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    // Method to create database
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        SQLiteDatabase db_Read = null;

        // If database exist delete database and copy the new one
        if(dbExist){
            // Do nothing - database already exist
        }else{
            db_Read = this.getReadableDatabase();
            db_Read.close();

            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    // Method to check database on path
    private boolean checkDataBase(){
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    // Method to copy database from app to db path
    private void copyDataBase() throws IOException {

        InputStream myInput = context.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

    }


    // Method to open database and read it
    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    // Method to close database after it is used
    @Override
    public void close() {
        db.close();
    }


    // Method to check whether database is open
    public boolean isDatabaseOpen(){
        if(db.isOpen())
            return true;
        else
            return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


    // Method to get all recipes data from database
    public ArrayList<ArrayList<Object>> getAllRecipesData(){
        ArrayList<ArrayList<Object>> dataArrays= new ArrayList<ArrayList<Object>>();

        Cursor cursor = null;

        try{
            cursor = db.query(
                    TABLE_RECIPES,
                    new String[]{RECIPE_ID, RECIPE_NAME,RECIPE_PRICE, COOK_TIME, SERVINGS, IMAGE},
                    null, null, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()){
                do{
                    ArrayList<Object> dataList = new ArrayList<Object>();

                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getLong(2));
                    dataList.add(cursor.getString(3));
                    dataList.add(cursor.getString(4));
                    dataList.add(cursor.getString(5));

                    dataArrays.add(dataList);
                }

                while (cursor.moveToNext());
            }
            cursor.close();
        }catch (SQLException e){
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return dataArrays;
    }

    // Method to add recipe data to favorites database
    public boolean addRecipeToFavorites(String recipeId, String categoryId, String recipeName,String recipePrice,
                                        String cookTime, String servings, String summary,
                                        String ingredients, String steps, String image){

        boolean result;

        ContentValues values = new ContentValues();
        values.put(RECIPE_ID, recipeId);
        values.put(CATEGORY_ID, categoryId);
        values.put(RECIPE_NAME, recipeName);
        values.put(RECIPE_PRICE, recipePrice);
        values.put(COOK_TIME, cookTime);
        values.put(SERVINGS, servings);
        values.put(SUMMARY, summary);
        values.put(INGREDIENTS, ingredients);
        values.put(STEPS, steps);
        values.put(IMAGE, image);

        try{
            db.insert(TABLE_RECIPES, null, values);
            result = true;
        }
        catch(Exception e)
        {
            result = false;
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return result;
    }

    // Method to check whether recipe data is available in favorites database
    public boolean isDataAvailable(String id){
        boolean isAvailable = false;

        Cursor cursor = null;

        try{
            cursor = db.query(
                    TABLE_RECIPES,
                    new String[]{RECIPE_ID},
                    RECIPE_ID +" = "+ id,
                    null, null, null, null, null);


            if(cursor.getCount() > 0){
                isAvailable = true;
            }

            cursor.close();
        }catch (SQLException e){
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return isAvailable;
    }

    // Method to delete recipe data from favorite database
    public boolean deleteRecipeFromFavorites(String id){
        // Ask the database manager to delete the row of given id
        try {
            db.delete(TABLE_RECIPES, RECIPE_ID + "=" + id, null);
        }
        catch (Exception e)
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
