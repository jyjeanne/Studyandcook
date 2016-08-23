package fr.wayofcode.studyandcook.providers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by jeremy.
 *
 *  ProviderSuggestion is created to provide search suggestion to search menu item on actionbar.
 * Created using SearchRecentSuggestionsProvider.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.pongodev.recipesapp.providers.ProviderSuggestion";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider(){
        setupSuggestions(AUTHORITY, MODE);
    }
}
