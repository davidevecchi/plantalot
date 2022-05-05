package com.plantalot.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.plantalot.R;

import java.util.List;

// Cambia il contenuto del backlyer
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {
	
	private final List<String> mData;
	private final LayoutInflater mInflater;
	private ItemClickListener mClickListener;
	
	// data is passed into the constructor
	public DrawerAdapter(Context context, List<String> data) {
		this.mInflater = LayoutInflater.from(context);
		this.mData = data;
	}
	
	// inflates the row layout from xml when needed
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = mInflater.inflate(R.layout.home_bl_drawer_button, parent, false);
		return new ViewHolder(view);
	}
	
	// binds the data to the TextView in each row
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		String giardino = mData.get(position);
		holder.button.setText(giardino);
	}
	
	// total number of rows
	@Override
	public int getItemCount() {
		return mData.size();
	}
	
	
	// stores and recycles views as they are scrolled off screen
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		Button button;
		
		ViewHolder(View itemView) {
			super(itemView);
			button = itemView.findViewById(R.id.drawer_button_text);
			itemView.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View view) {
			if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
		}
	}
	
	// convenience method for getting data at click position
	String getItem(int id) {
		return mData.get(id);
	}
	
	// allows clicks events to be caught
	void setClickListener(ItemClickListener itemClickListener) {
		this.mClickListener = itemClickListener;
	}
	
	// parent activity will implement this method to respond to click events
	public interface ItemClickListener {
		void onItemClick(View view, int position);
	}
	
}
