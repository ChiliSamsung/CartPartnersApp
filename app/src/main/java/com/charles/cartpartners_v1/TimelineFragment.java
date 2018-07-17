package com.charles.cartpartners_v1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.charles.cookingapp.R;
import java.util.ArrayList;

public class TimelineFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.timeline_layout, container, false);

        //will call the getParcelableExtra method of the intent object to start the process?
        ArrayList<Item> bundledItems;
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> quantities = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            bundledItems = bundle.getParcelableArrayList("ItemsList");

            for(Item i : bundledItems) {
                names.add(i.getName());
                prices.add("" + i.getPrice());
                dates.add(i.getDate());
                quantities.add("" + i.getQuantity());
                types.add(i.getType());
            }

            recyclerView = rootView.findViewById(R.id.timelineRecycler);
            layoutManager = new LinearLayoutManager(rootView.getContext());
            recyclerView.setLayoutManager(layoutManager);
            TimelineAdapter timelineAdapter = new TimelineAdapter(names, dates, quantities, types, prices);
            recyclerView.setAdapter(timelineAdapter);
        }
        return rootView;
    }
}
