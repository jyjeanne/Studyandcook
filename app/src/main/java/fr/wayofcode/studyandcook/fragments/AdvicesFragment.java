package fr.wayofcode.studyandcook.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.wayofcode.studyandcook.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdvicesFragment } interface
 * to handle interaction events.
 * Use the {@link AdvicesFragment#} factory method to
 * create an instance of this fragment.
 */
public class AdvicesFragment extends Fragment {


    public AdvicesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advices, container, false);

    }


}
