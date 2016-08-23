package fr.wayofcode.studyandcook.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.fragments.AdvicesFragment;
import fr.wayofcode.studyandcook.fragments.CategoriesFragment;
import fr.wayofcode.studyandcook.fragments.ToolsFragment;

public class MenuActivity extends AppCompatActivity {

    public static final int NEW_ALARM = 1;

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Connect toolbar object with toolbar id in xml
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // Set toolbar as actionbar
        setSupportActionBar(mToolbar);

        // Set logo to toolbar
        mToolbar.setLogo(R.mipmap.ic_logo);

        // Handle item menu in toolbar
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menuSearch:
                        return true;
                    case R.id.menuFavorites:
                        // Open ActivityFavorites
                        Intent favoritesIntent = new Intent(getApplicationContext(),
                                FavoritesActivity.class);
                        startActivity(favoritesIntent);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        return true;
                    case R.id.menuAbout:
                        // Open ActivityAbout
                        Intent aboutIntent = new Intent(getApplicationContext(),
                                AboutActivity.class);
                        startActivity(aboutIntent);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                        return true;
                    default:
                        return true;
                }
            }
        });

        // Set the floating button action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //startActivity(new Intent(view.getContext(), CreateActivity.class));
                Intent intent = new Intent(view.getContext(), NewRecipeActivity.class);
                startActivityForResult(intent, NEW_ALARM);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);

            }
        });

        // init TabHost with categories
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.first_tab_category)).setIndicator("Recipes"),
                CategoriesFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.second_tab_category)).setIndicator("Advices"),
                AdvicesFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.third_tab_category)).setIndicator("Tools"),
                ToolsFragment.class, null);



    }

    // Method to add actionbar item menu layout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        // Get the SearchView and set the searchable configuration.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menuSearch).getActionView();
        // Assumes current activity is the searchable activity.
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // Do not iconify the widget; expand it by default.
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
