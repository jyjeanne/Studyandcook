package fr.wayofcode.studyandcook.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.activities.HomeActivity;

/**
 * A simple {@link Fragment} subclass.

 */
public class CategoriesFragment extends ListFragment  implements AdapterView.OnItemClickListener {

    // constants
    public static final int DIVIDER_HEIGHT = 1;
    public static final int LIST_RECIPES_TEXT_HEIGHT = 9;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        String[] arr = getResources().getStringArray(R.array.list_recipes_categories);

        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,arr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                text.setHeight(LIST_RECIPES_TEXT_HEIGHT);
                text.setTypeface(text.getTypeface(), Typeface.BOLD);
                return view;
            }
        };

        setListAdapter(adapter);
        ColorDrawable dividerDrawable = new ColorDrawable(this.getResources().getColor(R.color.divider_color));
        getListView().setDivider(dividerDrawable);
        getListView().setDividerHeight(DIVIDER_HEIGHT);

        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

         switch (position)
         {
             case 0:  Intent newActivity = new Intent(getActivity(), HomeActivity.class);
                 startActivity(newActivity);
                 // Add a custom activity transition
                 // source : http://www.warpdesign.fr/android-replace-default-transitions-with-slides-in-activities/
                 getActivity().overridePendingTransition(R.anim.open_next, R.anim.close_main);
                 break;

         }
       

    }
}
