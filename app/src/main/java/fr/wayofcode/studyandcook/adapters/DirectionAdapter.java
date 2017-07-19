package fr.wayofcode.studyandcook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fr.wayofcode.studyandcook.R;
import fr.wayofcode.studyandcook.model.Direction;

/**
 * Created by jeremy on 28/09/16.
 */

public class DirectionAdapter extends ArrayAdapter<Direction> {
    private final Context context;
    private final ArrayList<Direction> modelsArrayList;

    public DirectionAdapter(Context context, ArrayList<Direction> modelsArrayList) {

        super(context, R.layout.row_direction, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;

            rowView = inflater.inflate(R.layout.row_direction, parent, false);

            // 3. Get icon,title & counter views from the rowView
            TextView counterView = (TextView) rowView.findViewById(R.id.direction_counter);
            TextView titleView = (TextView) rowView.findViewById(R.id.direction_text);


            // 4. Set the text for textView
            titleView.setText(modelsArrayList.get(position).getDescritpion());
            counterView.setText(modelsArrayList.get(position).getStringOrder());



        // 5. retrn rowView
        return rowView;
    }
}
