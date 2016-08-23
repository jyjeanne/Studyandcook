package fr.wayofcode.studyandcook.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import fr.wayofcode.studyandcook.R;

/**
 * SplashActivity is created to display welcome screen with logo.
 * Created using AppCompatActivity.
 */
public class SplashActivity extends AppCompatActivity {

    // temporisation time in secondes
    public static final int TEMPO_SPLASH_SCREEN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Configuration in Android API below 21 to set window to full screen.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // Create loading to wait for few second before displaying HomeActivity
        new Loading().execute();
    }

    // Inner Asynctask class to process loading in background
    public class Loading extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(TEMPO_SPLASH_SCREEN);
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // When progress finished, open HomeActivity
            Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
            startActivity(menuIntent);
            // Add a custom activity transition
            // source : http://www.warpdesign.fr/android-replace-default-transitions-with-slides-in-activities/
            overridePendingTransition(R.anim.open_next, R.anim.close_main);
        }
    }

    // Configuration in Android API 21 to set window to full screen.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }
}
