package fr.wayofcode.studyandcook.activities;

import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import fr.wayofcode.studyandcook.R;

/**
 * ActivityBase is created to handle scrolling page event.
 * Modified from ObservableScrollView library.
 */
public class BaseActivity extends AppCompatActivity {

    // Method to get Actionbar size
    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    // Method to get screen height
    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }
}
