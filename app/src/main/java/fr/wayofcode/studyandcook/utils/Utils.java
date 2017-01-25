package fr.wayofcode.studyandcook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by jerem_000 on 22/05/2016.
 */
public class Utils {
    // Application parameters. do not change this parameters.
    public static final String PRICE_FORMAT = "%.2f";
    public static final String ARG_PAGE = "page";
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_SEARCH = "search";
    public static final String ARG_FAVORITES = "favorites";
    public static final String ARG_KEY = "key";
    public static final String ARG_CONTENT = "content";
    public static final String ARG_PARENT_ACTIVITY = "parent_activity";
    public static final String ARG_ACTIVITY_HOME = "activities.HomeActivity";
    public static final String ARG_ACTIVITY_SEARCH = "activities.SearchActivity";
    public static final String ARG_ACTIVITY_FAVORITES = "activities.FavoritesActivity";
    public static final String ARG_TRIGGER = "trigger";
    public static final String PICTURE_EXTERNAL_FOLDER = "/DirName";

    // Configurable parameters. you can configure these parameter.
    // Set database path. Change  fr.wayofcode.studyandcook with your own package name.
    // It must be equal with package name.
    public static final String ARG_DATABASE_PATH = "/data/data/fr.wayofcode.studyandcook/databases/";
    // For every recipe detail you want to display interstitial ad.
    // 3 means interstitial ad will display after user open detail page three times.
    public static final int ARG_TRIGGER_VALUE = 3;
    // Admob visibility parameter. set true to show admob and false to hide.
    public static final boolean IS_ADMOB_VISIBLE = true;
    // Set value to true if you are still in development process,
    // and false if you are ready to publish the app.
    public static final boolean IS_ADMOB_IN_DEBUG = false;
    // Set default category data, you can see the category id in sqlite database.
    public static final String ARG_DEFAULT_CATEGORY_ID = "2";


    // Method to check admob visibility
    public static boolean admobVisibility(AdView ad, boolean isInDebugMode){
        if(isInDebugMode) {
            ad.setVisibility(View.VISIBLE);
            return true;
        }else {
            ad.setVisibility(View.GONE);
            return false;
        }
    }

    // Method used to save picture
    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + PICTURE_EXTERNAL_FOLDER);

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard"+PICTURE_EXTERNAL_FOLDER+"/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard"+PICTURE_EXTERNAL_FOLDER+"/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to convert price to correct format
    public static String formatRecipePrice(String textNumber) {
        float number=Float.parseFloat(textNumber)/100f;

        return String.format(PRICE_FORMAT,number);

    }

    // Method to load data that stored in SharedPreferences
    public static int loadPreferences(String param, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("user_data", 0);
        return sharedPreferences.getInt(param, 0);
    }

    // Method to save data to SharedPreferences
    public static void savePreferences(String param, int value, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("user_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(param, value);
        editor.apply();
    }

    // Method to check permission
    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }
}
