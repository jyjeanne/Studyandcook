package fr.wayofcode.studyandcook.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.lb.material_preferences_library.PreferenceActivity;
import com.lb.material_preferences_library.custom_preferences.Preference;

import fr.wayofcode.studyandcook.R;

/**
 * ActivityAbout is created to display app information such as app name,
 * version, and developer name. Created using PreferenceActivity.
 */
public class AboutActivity extends PreferenceActivity
        implements Preference.OnPreferenceClickListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        // Set preference theme, you can configure the color theme
        // via res/values/styles.xml
        setTheme(R.style.AppTheme_Light);
        super.onCreate(savedInstanceState);

        // Connect preference objects with preference key in xml file
        Preference mPrefShareKey = (Preference) findPreference(getString(R.string.pref_share_key));
        Preference mPrefRateReviewKey = (Preference) findPreference(getString(R.string.pref_rate_review_key));

        // Set preference click listener to the preference objects
        mPrefShareKey.setOnPreferenceClickListener(this);
        mPrefRateReviewKey.setOnPreferenceClickListener(this);

    }

    @Override
    protected int getPreferencesXmlId()
    {
        // Connect preference activity with preference xml
        return R.xml.pref_about;
    }


    @Override
    public boolean onPreferenceClick(android.preference.Preference preference) {
        switch(preference.getKey()){
            case "prefShareKey":
                // Share Google Play url via other apps such as message, email, facebook, etc.
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message) +
                        " " + getString(R.string.google_play_url));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
                break;
            case "prefRateReviewKey":
                // Open App page on Google Play so that user can rate and review the app.
                Intent rateReviewIntent = new Intent(Intent.ACTION_VIEW);
                rateReviewIntent.setData(Uri.parse(getString(R.string.google_play_url)));
                startActivity(rateReviewIntent);
                break;
        }
        return true;
    }
}
