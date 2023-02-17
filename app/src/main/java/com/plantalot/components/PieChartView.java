package com.plantalot.components;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.plantalot.R;
import com.plantalot.adapters.ChartLegendAdapter;
import com.plantalot.classes.PlantsCounter;
import com.plantalot.utils.Consts;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


@RequiresApi(api = Build.VERSION_CODES.N)
public class PieChartView extends LinearLayout {  // TODO extends View
	
	private PieChart mPieChart;
	private final Context context;
	private final View view;
	private PlantsCounter plantsCounter;
	private ArrayList<Pair<String, Integer>> buttons = new ArrayList<>(Arrays.asList(
			new Pair<>("Area", R.drawable.ic_round_square_foot_24),
			new Pair<>("Piante", R.drawable.ic_round_numbers_24),
			new Pair<>("Produzione", R.drawable.ic_round_scale_24)
	));
	private int buttonIdx = -1;
	
	private LinearLayout mBtn;
	private TextView mLabel;
	private ImageView mIcon;
	
	
	public PieChartView(Context context) {
		super(context);
		this.context = context;
		view = inflate(context, R.layout.piechart_with_legend, this);
		setupButton();
	}
	
	public PieChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		view = inflate(context, R.layout.piechart_with_legend, this);
		setupButton();
	}
	
	
	private void setupButton() {
		mBtn = view.findViewById(R.id.piechart_btn);
		mLabel = view.findViewById(R.id.piechart_btn_label);
		mIcon = view.findViewById(R.id.piechart_btn_icon);
		mBtn.setOnClickListener(v -> updateButton());
		updateButton();
	}
	
	private void updateButton() {
		buttonIdx = (buttonIdx + 1) % buttons.size();
		mLabel.setText(buttons.get(buttonIdx).first);
		mIcon.setImageResource(buttons.get(buttonIdx).second);
		if (plantsCounter != null) updateContent(plantsCounter);
	}
	
	public void updateContent(PlantsCounter plantsCounterNew) {
		plantsCounter = new PlantsCounter(plantsCounterNew);
		HashMap<String, Integer> famiglie = plantsCounter.getFamiglieMeasure(buttonIdx);
		
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
