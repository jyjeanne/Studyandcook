package fr.wayofcode.studyandcook.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.io.IOException;
import java.util.ArrayList;

import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.adapters.RecipesAdapter;
import fr.wayofcode.studyandcook.databases.DbHelperFavorites;
import fr.wayofcode.studyandcook.databases.DbHelperRecipes;
import fr.wayofcode.studyandcook.listeners.OnTapListener;
import fr.wayofcode.studyandcook.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipesFragment OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipesFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipesFragment extends Fragment {
    // Create view objects
    private RecyclerView mList;
    private CircleProgressBar mPrgLoading;
    private TextView mTxtEmpty;
    //private AdView mAdView;

    // Create instance of database helper
    private DbHelperRecipes mDBhelperRecipes;
    private DbHelperFavorites mDBhelperFavorites;
    private RecipesAdapter mAdapterRecipes;

    private OnRecipeSelectedListener mCallback;

    private ArrayList<ArrayList<Object>> mData;

    private String mCurrentKey = Utils.ARG_DEFAULT_CATEGORY_ID;
    private String mActivePage = Utils.ARG_CATEGORY;

    private boolean mIsAdmobVisible;

    // Create arraylist variables to store data
    private ArrayList<String> mRecipeIds = new ArrayList<>();
    private ArrayList<String> mRecipePrices = new ArrayList<>();
    private ArrayList<String> mRecipeNames = new ArrayList<>();
    private ArrayList<String> mCookTimes = new ArrayList<>();
    private ArrayList<String> mServings = new ArrayList<>();
    private ArrayList<String> mRecipeImages = new ArrayList<>();


    // Interface, activity that use FragmentRecipes must implement onRecipeSelecte method
    public interface OnRecipeSelectedListener {
        void onRecipeSelected(String ID, String CategoryName);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current item selection in case we need to recreate the fragment
        outState.putString(Utils.ARG_KEY, mCurrentKey);
        outState.putString(Utils.ARG_PAGE, Utils.ARG_CATEGORY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // If saveInstanceSate is not null than use data from savedInstanceState
        if (savedInstanceState != null) {
            mCurrentKey = savedInstanceState.getString(Utils.ARG_KEY);
            mActivePage = savedInstanceState.getString(Utils.ARG_PAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set fragment layout
        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);

        if (savedInstanceState != null) {
            mCurrentKey = savedInstanceState.getString(Utils.ARG_KEY);
            mActivePage = savedInstanceState.getString(Utils.ARG_PAGE);
        }


        // Connect view objects and view id on xml.
        mList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mPrgLoading = (CircleProgressBar) rootView.findViewById(R.id.prgLoading);
        mTxtEmpty = (TextView) rootView.findViewById(R.id.txtEmpty);
        //mAdView = (AdView) rootView.findViewById(R.id.adView);

        //mIsAdmobVisible = Utils.admobVisibility(mAdView, Utils.IS_ADMOB_VISIBLE);

        mPrgLoading.setColorSchemeResources(R.color.accent_color);
        mList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(layoutManager);

        // Load ad in background using asynctask class
        //new SyncShowAd(mAdView).execute();

        // Create object of database helpers.
        mDBhelperRecipes = new DbHelperRecipes(getActivity());
        mDBhelperFavorites = new DbHelperFavorites(getActivity());

        // Create database of recipes.
        try {
            mDBhelperRecipes.createDataBase();
            mDBhelperFavorites.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        openDatabase();

        mAdapterRecipes = new RecipesAdapter(getActivity());

        // When item on list selected, send recipe id and to onRecipeSelected method
        mAdapterRecipes.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(String ID, String CategoryName) {
                mCallback.onRecipeSelected(ID, "");
            }
        });

        return rootView;
    }




    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
        Bundle args = getArguments();
        if (args != null) {
            // Set recipes based on argument passed in
            updateRecipes(args.getString(Utils.ARG_KEY), args.getString(Utils.ARG_PAGE));
        } else if (!mCurrentKey.equals("")) {
            // Set recipes based on saved instance state defined during onCreateView
            updateRecipes(mCurrentKey, mActivePage);
        }
    }

    public void openDatabase(){
        mDBhelperRecipes.openDataBase();
        mDBhelperFavorites.openDataBase();
    }

    // Method to update recipe list
    public void updateRecipes(String key, String page){

        openDatabase();
        mCurrentKey = key;
        mActivePage = page;

        new SyncGetData().execute();

    }

    // Asynctask class to retrieve data from database in background
    public class SyncGetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // When data still retrieve from database display loading view,
            // clear previous variables, and hide other view
            mPrgLoading.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
            mTxtEmpty.setVisibility(View.GONE);
            mRecipeIds.clear();
            mRecipePrices.clear();
            mRecipeNames.clear();
            mCookTimes.clear();
            mServings.clear();
            mRecipeImages.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Get data from database
            getDataFromDatabase(mCurrentKey);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Hide loading view and display other views
            mPrgLoading.setVisibility(View.GONE);

            // If no data in database display "empty" text, else display data
            if (mRecipeIds.isEmpty()) {
                mTxtEmpty.setVisibility(View.VISIBLE);
            } else {
                mTxtEmpty.setVisibility(View.GONE);
                mList.setVisibility(View.VISIBLE);
                mAdapterRecipes.updateList(mRecipeIds, mRecipeNames, mRecipePrices, mCookTimes, mServings, mRecipeImages);
            }
            mList.setAdapter(mAdapterRecipes);


        }
    }

    // Method to retrieve data from database
    public void getDataFromDatabase(String key){

        if(mActivePage.equals(Utils.ARG_CATEGORY)) {
            mData = mDBhelperRecipes.getAllRecipesData(key);
        }else if(mActivePage.equals(Utils.ARG_SEARCH)){
            mData = mDBhelperRecipes.getRecipesByName(key);
        }else if(mActivePage.equals(Utils.ARG_FAVORITES)){
            mData = mDBhelperFavorites.getAllRecipesData();
        }

        int dataSize = mData.size();
        for(int i = 0;i < dataSize;i++){
            ArrayList<Object> row = mData.get(i);

            mRecipeIds.add(row.get(0).toString());
            mRecipeNames.add(row.get(1).toString());
            // TODO add format price
            mRecipePrices.add(Utils.formatRecipePrice(row.get(2).toString()));
            mCookTimes.add(row.get(3).toString());
            mServings.add(row.get(4).toString());
            mRecipeImages.add(row.get(5).toString());
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnRecipeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRecipeSelectedListener");
        }
    }




    /** Called when leaving the activity */
    @Override
    public void onPause() {

        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {

        mAdapterRecipes.notifyDataSetChanged();
        super.onResume();
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
