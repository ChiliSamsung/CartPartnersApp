package com.charles.cartpartners_v1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.charles.cookingapp.R;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private ArrayList<String> dates;
    private ArrayList<String> names;
    private ArrayList<String> quantities;
    private ArrayList<String> types;
    private ArrayList<String> prices;

    TimelineAdapter(ArrayList<String> names, ArrayList<String> dates, ArrayList<String> quantities,
                           ArrayList<String> types, ArrayList<String> prices) {
        this.dates = dates;
        this.names = names;
        this.quantities = quantities;
        this.types = types;
        this.prices = prices;
    }

    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_entry_layout, parent, false);
        return new TimelineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TimelineAdapter.ViewHolder holder, final int position) {
        final String name = names.get(position);
        holder.timelineName.setText(name);
        final String price = "$" + prices.get(position);
        holder.timelinePrice.setText(price);
        final String date = dates.get(position);
        holder.timelineDate.setText(date);
        final String quantity = "Qty: " + quantities.get(position);
        holder.timelineQty.setText(quantity);
        final String type = types.get(position);
        holder.timelineType.setText(type);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timelineName)
        TextView timelineName;
        @BindView(R.id.timelinePrice)
        TextView timelinePrice;
        @BindView(R.id.timelineDate)
        TextView timelineDate;
        @BindView(R.id.timelineQty)
        TextView timelineQty;
        @BindView(R.id.timelineType)
        TextView timelineType;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
