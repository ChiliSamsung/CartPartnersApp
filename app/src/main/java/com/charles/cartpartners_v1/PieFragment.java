package com.charles.cartpartners_v1;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.charles.cookingapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PieFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    //TODO: improve pie chart
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pie_layout, container, false);

        //will call the getParcelableExtra method of the intent object to start the process?
        Bundle bundle = this.getArguments();

        PieChart chart = rootView.findViewById(R.id.pieChart);
        chart.setRotationEnabled(true);

        HashMap<String, Float> typeToTotalValue = new HashMap<>();
        ArrayList<PieEntry> data = new ArrayList<>(); //custom data type
        ArrayList<Item> bundledItems = bundle.getParcelableArrayList("ItemsList");

        for (int i = 0; i < bundledItems.size(); i++) {
            String itemType = bundledItems.get(i).getType();
            float value = (float) bundledItems.get(i).getTotalValue();
            if (typeToTotalValue.containsKey(itemType)) {
                typeToTotalValue.put(itemType, typeToTotalValue.get(itemType) + value);
            } else {
                typeToTotalValue.put(itemType, value);
            }
        }

        Set<Map.Entry<String,Float>> itemEntries = typeToTotalValue.entrySet();

        int i = 0;
        for (Map.Entry<String, Float> e : itemEntries) {
            PieEntry pieEntry = new PieEntry(e.getValue(), i);
            pieEntry.setLabel(e.getKey());
            data.add(pieEntry);
            i++;
        }

        PieDataSet pieDataSet = new PieDataSet(data, "Total Sales by Item Type");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.WHITE);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.argb(100, 204, 102, 0));
        colors.add(Color.argb(100, 0, 102, 0));
        colors.add(Color.argb(100, 158, 90, 179));
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);




        //built - in legend
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setTextColor(Color.DKGRAY);

        PieData pieData = new PieData(pieDataSet);
        chart.setData(pieData);

        Description d = new Description();
        d.setText("Dollars");
        d.setTextSize(12);
        chart.setDescription(d);

        chart.invalidate();

        return rootView;
    }

}
