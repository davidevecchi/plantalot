package com.plantalot.adapters;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.plantalot.R;
import com.plantalot.fragments.AllPlantsFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;


// Cambia il contenuto del backlyer
public class AllPlantsFiltersAdapter extends RecyclerView.Adapter<AllPlantsFiltersAdapter.ViewHolder> {
	
	private final List<Pair<String, List<String>>> mData;
	private final LayoutInflater mInflater;
	private final String RAGGRUPPA;
	private final Context context;
	private final AllPlantsFragment fragment;
	private final HashMap<String, Set<String>> activeFilters;
	private final HashMap<String, String> titles;
	
	// data is passed into the constructor
	public AllPlantsFiltersAdapter(Context context, HashMap<String, Set<String>> activeFilters,
	                               List<Pair<String, List<String>>> data, String raggruppa,
	                               AllPlantsFragment fragment, HashMap<String, String> titles) {
		this.mData = data;
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.fragment = fragment;
		this.titles = titles;
		this.activeFilters = activeFilters;
		this.RAGGRUPPA = raggruppa;
		System.out.println(activeFilters);
		System.out.println(mData);
	}
	
	// inflates the row layout from xml when needed
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.all_plants_bl_drawer_chips, parent, false);
		fragment.setupBackButtonHandler();
		return new ViewHolder(view);
	}
	
	// binds the data to the TextView in each row
	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		String title = mData.get(position).first;
		List<String> chips = mData.get(position).second;
		viewHolder.title.setText(titles.get(title));
		if (Objects.equals(title, RAGGRUPPA)) {
			viewHolder.chipGroup.setSingleSelection(true);
			viewHolder.chipGroup.setSelectionRequired(true);
		}
		viewHolder.chipGroup.removeAllViews();
		for (String c : chips) {
			if (c.isEmpty()) {
				viewHolder.chipGroup.addView(mInflater.inflate(R.layout.component_chips_divider, null, false));
			} else {
				Chip chip = new Chip(context);
				chip.setText(Objects.equals(title, RAGGRUPPA) ? titles.get(c) : c);
				chip.setCheckable(true);

				chip.setChipBackgroundColor(context.getResources().getColorStateList(R.color.chip_background_color));
				chip.setTextColor(context.getResources().getColorStateList(R.color.chip_text_color));

//				if (Objects.equals(title, RAGGRUPPA)) {
				for (String value : activeFilters.get(title)) {
					if (Objects.equals(value, c)) chip.setChecked(true);
				}
				chip.setOnCheckedChangeListener((changedChip, b) -> {
					if (changedChip.isChecked()) {
						activeFilters.get(title).add(c);
					} else {
						activeFilters.get(title).remove(c);
					}
				});
				chip.setOnClickListener(l -> {
					System.out.println(l.getClass().getName());
					if (!(Objects.equals(title, RAGGRUPPA) && activeFilters.get(title).size() != 1)) {
						new Handler().post(fragment::queryDb);  // FIXME !!!???
					}
				});
				viewHolder.chipGroup.addView(chip);
			}
		}
	}
	
	// total number of rows
	@Override
	public int getItemCount() {
		return mData.size();
	}
	
	
	// stores and recycles views as they are scrolled off screen
	public static class ViewHolder extends RecyclerView.ViewHolder {
		TextView title;
		ChipGroup chipGroup;
		
		public ViewHolder(View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.all_plants_bl_drawer_chips_title);
			chipGroup = itemView.findViewById(R.id.all_plants_bl_drawer_chips_chipgroup);
		}
	}
}
