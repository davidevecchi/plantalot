package com.plantalot.adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.plantalot.R;
import com.plantalot.components.OrtaggioSpecs;
import com.plantalot.utils.Consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Riempe la card con le icone
public class ChartLegendAdapter extends RecyclerView.Adapter<ChartLegendAdapter.ViewHolder> {

    private final ArrayList<String> famiglie;

    public ChartLegendAdapter(@NonNull ArrayList<String> famiglie) {
        this.famiglie = famiglie;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chart_legend_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String famiglia = famiglie.get(position);
        viewHolder.mTextView.setText(famiglia);
        viewHolder.mColorView.setBackgroundColor(Consts.colors.get(famiglia));
    }

    @Override
    public int getItemCount() {
        return famiglie.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        View mColorView;

        ViewHolder(View view) {
            super(view);
            mTextView = view.findViewById(R.id.chart_legend_item_text);
            mColorView = view.findViewById(R.id.chart_legend_item_color);
        }
    }

}
