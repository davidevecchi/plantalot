package com.plantalot.components;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.plantalot.R;
import com.plantalot.adapters.ChartLegendAdapter;
import com.plantalot.utils.Consts;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PieChartView extends LinearLayout {  // TODO extends View

    private PieChart mPieChart;
    private final Context context;
   private final View view;

    public PieChartView(Context context) {
        super(context);
        this.context = context;
        view = inflate(context, R.layout.piechart_with_legend, this);
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        view = inflate(context, R.layout.piechart_with_legend, this);
    }

    public void update(HashMap<String, Integer> famiglie) {
        view.setVisibility(famiglie.isEmpty() ? GONE : VISIBLE);
        if (famiglie.isEmpty()) return;

        ArrayList<String> famiglieList = new ArrayList<>(famiglie.keySet());
        Collections.sort(famiglieList);

        mPieChart = view.findViewById(R.id.piechart);
        RecyclerView mLegendRecycler = view.findViewById(R.id.chart_legend_recycler);

        mPieChart.clearChart();
        for (String f : famiglieList) {
            mPieChart.addPieSlice(new PieModel(f, famiglie.get(f), Consts.colors.get(f)));
        }
        mPieChart.update();

        ChartLegendAdapter legendAdapter = new ChartLegendAdapter(new ArrayList<>(famiglieList));
        mLegendRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mLegendRecycler.setAdapter(legendAdapter);
    }

    public void startAnimation() {
        mPieChart.startAnimation();
    }


}
