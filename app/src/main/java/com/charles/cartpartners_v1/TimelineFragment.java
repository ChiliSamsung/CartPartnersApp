package com.charles.cartpartners_v1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charles.cookingapp.R;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment {

    private List<Item> salesData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.timeline_layout, container, false);
        TextView timelineView = rootView.findViewById(R.id.timelineView);

        //will call the getParcelableExtra method of the intent object to start the process?
        ArrayList<Item> bundledItems;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bundledItems = bundle.getParcelableArrayList("ItemsList");

            StringBuilder builder = new StringBuilder();

            for(Item i : bundledItems) {
                builder.append(i.getTimelineFormat());
                builder.append("\n");
            }

            timelineView.setText(builder.toString());
        }

        return rootView;
    }
}
