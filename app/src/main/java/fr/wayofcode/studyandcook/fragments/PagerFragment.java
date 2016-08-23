package fr.wayofcode.studyandcook.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PagerFragment OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PagerFragment extends Fragment {
    // Create variable to store data
    private String mContent;

    // Method to create FragmentPager and pass data
    public static PagerFragment newInstance(String summary) {
        PagerFragment fragment = new PagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.ARG_CONTENT, summary);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Store data that pass from activity to variable
        mContent = getArguments().getString(Utils.ARG_CONTENT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set fragment layout
        View rootView = inflater.inflate(R.layout.fragment_pager,container,false);

        // Connect HTMLTextView object with view id in xml
        HtmlTextView mTxtContent = (HtmlTextView) rootView.findViewById(R.id.txtContent);

        // Set data to HTMLTextView
        mTxtContent.setHtmlFromString(mContent, true);

        return rootView;
    }

}
