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

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.io.IOException;
import java.util.ArrayList;

import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.adapters.CategoriesAdapter;
import fr.wayofcode.studyandcook.databases.DbHelperRecipes;
import fr.wayofcode.studyandcook.listeners.OnTapListener;

/**
 * FragmentCategory is created to display category list.
 * Created using Fragment.
 */
public class RecipesCategoriesFragment extends Fragment {
    // Create view objects
    private RecyclerView mList;
    private CircleProgressBar mPrgLoading;

    // Create instance of database helper
    private DbHelperRecipes mDBhelperRecipes;

    // Create instance of adapter
    private CategoriesAdapter mAdapterCategories;

    private OnCategorySelectedListener mCallback;

    // Create arraylist variables to store data.
    private ArrayList<String> mCategoryIds = new ArrayList<>();
    private ArrayList<String> mCategoryNames = new ArrayList<>();

    // Create interface listener.
    public interface OnCategorySelectedListener{
        void onCategorySelected(String ID, String CategoryName);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set fragment layout
        View rootView = inflater.inflate(R.layout.fragment_recipes_categories, container, false);

        // Connect view objects with view id in xml
        mList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mPrgLoading = (CircleProgressBar) rootView.findViewById(R.id.prgLoading);

        // Set recyclerview layout
        mList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mList.setLayoutManager(layoutManager);

        // Create object of database helpers
        mDBhelperRecipes = new DbHelperRecipes(getActivity());

        // Create recipes database
        try {
            mDBhelperRecipes.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }

        // Open recipes database
        mDBhelperRecipes.openDataBase();

        mAdapterCategories = new CategoriesAdapter(getActivity());

        // Retrieve data from database in background using asynctask
        new syncGetData().execute();

        // When item on list selected, send category id and name to onCategorySelected method
        mAdapterCategories.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(String ID, String CategoryName) {
                mCallback.onCategorySelected(ID, CategoryName);
            }
        });

        return rootView;
    }

    // Asynctask class to handle load data from database in background
    public class syncGetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // When data still retrieve from database display loading view
            // and hide other view
            mPrgLoading.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
            mCategoryIds.clear();
            mCategoryNames.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get data from database
            getDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Hide loading view and display other views
            mPrgLoading.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
            mAdapterCategories.updateList(mCategoryIds, mCategoryNames);

            // When finished retrieve data from database, display data to the views
            mList.setAdapter(mAdapterCategories);

        }
    }

    // Get data from database and store to arraylist variables
    public void getDataFromDatabase(){
        ArrayList<ArrayList<Object>> mData = mDBhelperRecipes.getAllCategoriesData();

        int dataSize = mData.size();
        for(int i = 0; i< dataSize; i++){
            ArrayList<Object> row = mData.get(i);

            mCategoryIds.add(row.get(0).toString());
            mCategoryNames.add(row.get(1).toString());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnCategorySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategorySelectedListener");
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDBhelperRecipes.close();
    }
}
