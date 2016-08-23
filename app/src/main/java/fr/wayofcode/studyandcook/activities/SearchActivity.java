package fr.wayofcode.studyandcook.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.fragments.RecipesFragment;
import fr.wayofcode.studyandcook.providers.SuggestionProvider;
import fr.wayofcode.studyandcook.utils.Utils;

/**
 * ActivitySearch is created to display search result of recipes. Use FragmentRecipes to display
 * recipes list. Created using AppCompatActivity.
 */
public class SearchActivity extends AppCompatActivity implements
        RecipesFragment.OnRecipeSelectedListener{

    // Create variable to store data
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            keyword = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(keyword, null);
        }
        setContentView(R.layout.activity_search);


        // Connect toolbar object with toolbar id in xml
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // Set toolbar title with keyword
        mToolbar.setTitle(keyword);
        // Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create the FragmentRecipes and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Utils.ARG_KEY, keyword);
            arguments.putString(Utils.ARG_PAGE, Utils.ARG_SEARCH);
            RecipesFragment fragment = new RecipesFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }

        // Handle item menu in toolbar
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case android.R.id.home:
                        finish();
                        overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    /**
     * Callback method from {@link RecipesFragment.OnRecipeSelectedListener}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onRecipeSelected(String ID, String CategoryName) {
        // Call ActivityDetail and pass recipe id to that activity
        Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        detailIntent.putExtra(Utils.ARG_KEY, ID);
        detailIntent.putExtra(Utils.ARG_PARENT_ACTIVITY, Utils.ARG_ACTIVITY_SEARCH);
        startActivity(detailIntent);
        overridePendingTransition(R.anim.open_next, R.anim.close_main);
    }

    // Method to handle physical back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Call transition when physical back button pressed
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
