package com.charles.cartpartners_v1;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.charles.cookingapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class GraphFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.graph_layout, container, false);
        LineChart chart = rootView.findViewById(R.id.salesChart);

        //will call the getParcelableExtra method of the intent object to start the process?
        ArrayList<Item> bundledItems;
        Bundle bundle = this.getArguments();
        ArrayList<Entry> entries = new ArrayList<>(); //PhilJay version of Entry for charts

        if (bundle != null) {
            bundledItems = bundle.getParcelableArrayList("ItemsList");

            //if there are no sales in the last "x" days, displays "no chart data available"
            if(bundledItems.isEmpty()) {
                return rootView;
            }

            long totalSales = 0;
            long firstTimeStamp = 0;
            /*
                1. Gets the oldest date as a Timestamp. This will have the lowest .getTime() value
                2. To scale the line graph correctly, all the Timestamps need to have this oldest .getTime() value subtracted from them
                3. This will put the oldest timestamp at x = 0, which is what we want
             */
            try {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date parsedDate = formatter.parse(bundledItems.get(bundledItems.size() - 1).getDate());
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                firstTimeStamp = timestamp.getTime();
            } catch (Exception e) {
            }

            //gets all the data, subtracts firstTimeStamp from each to scale it
            final ArrayList<String> dateLabels = new ArrayList<>();
            for(int i = bundledItems.size(); i > 0; i--) {
                try {
                    totalSales += bundledItems.get(i).getTotalValue();
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date parsedDate = formatter.parse(bundledItems.get(i).getDate());
                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                    entries.add(new Entry((timestamp.getTime() - firstTimeStamp) / 10000, totalSales));
                    dateLabels.add(bundledItems.get(i).getDate().substring(5, 10));
                } catch (Exception e) {
                }
            }

            //sort into ascending order
            Collections.sort(entries, new EntryXComparator());

            //attaches the data accumulated to the chart
            LineDataSet dataSet = new LineDataSet(entries, "Total Sales by Time");
            dataSet.setColor(Color.MAGENTA);
            dataSet.setValueTextColor(Color.BLUE);
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);

            //setup the xAxis labels
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if ((int) value / 100000 > dateLabels.size()) {
                        return "hello";
                    } else {
                        return dateLabels.get(((int) value) / 100000);
                    }
                }
            });
            xAxis.setLabelRotationAngle(0);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            //setup the description label
            Description d = new Description();
            d.setText("Dollars");
            d.setTextSize(12);
            chart.setDescription(d);
            chart.invalidate(); //refreshes
        }

        return rootView;
    }
}
