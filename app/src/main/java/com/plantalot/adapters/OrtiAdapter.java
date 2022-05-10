package com.plantalot.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.plantalot.utils.Consts;
import com.plantalot.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OrtiAdapter extends RecyclerView.Adapter<OrtiAdapter.ViewHolder> {
	
	private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
	private final Map<String, List<Integer>> mData;
	private final List<String> mKeys;
	Context context;
	
	public OrtiAdapter(Map<String, List<Integer>> data) {
		this.mData = data;
		this.mKeys = new ArrayList<>(mData.keySet());
		Collections.shuffle(mKeys);
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		context = viewGroup.getContext();
		View view = LayoutInflater.from(context).inflate(R.layout.home_fl_card_orto, viewGroup, false);
		return new ViewHolder(view);
	}
	
	// Assegna ad ogni card i vari elementi (nome e card adapter)
	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
		String ortoName = mKeys.get(i);
		int specie = mData.get(ortoName).size();
		
		// Set card menu
		viewHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PopupMenu popup = new PopupMenu(context, viewHolder.buttonViewOption);
				popup.inflate(R.menu.home_fl_card_menu);
				popup.setGravity(Gravity.END);
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
//                    case R.id.menu1:
//                        //handle menu1 click
//                        return true;
//                    case R.id.menu2:
//                        //handle menu2 click
//                        return true;
//                    case R.id.menu3:
//                        //handle menu3 click
//                        return true;
							default:
								return false;
						}
					}
				});
				popup.show();
			}
		});
		
		viewHolder.mFrameLayout.post(new Runnable() {  // needed to get the width (fixme ?)
			public void run() {
				int width = viewHolder.mFrameLayout.getWidth();
				int padding = viewHolder.mFrameLayout.getPaddingTop();
				int imgwidth = (width - 2 * padding) / (Consts.CARD_PLANTS / 2);
				
				FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
				layoutManager.setJustifyContent(JustifyContent.CENTER);
				CardAdapter cardAdapter = new CardAdapter(new ArrayList<>(mData.get(ortoName)), imgwidth);
				
				viewHolder.mRecyclerView.setLayoutManager(layoutManager);
				viewHolder.mRecyclerView.setAdapter(cardAdapter);
				viewHolder.mRecyclerView.setRecycledViewPool(viewPool);
				
				ViewGroup.LayoutParams params = viewHolder.mFrameLayout.getLayoutParams();
				params.height = 2 * ((width - 2 * padding) / (Consts.CARD_PLANTS / 2) + padding);
				viewHolder.mFrameLayout.setLayoutParams(params);
				if (specie == 5 || specie == 6) {  // compact view
					viewHolder.mFrameLayout.setPadding(padding + imgwidth / 2, padding, padding + imgwidth / 2, padding);
				}
				
				viewHolder.titleTextView.setText(ortoName);
				viewHolder.labelTextView.setText(specie + " specie");
			}
		});
		
	}
	
	@Override
	public int getItemCount() {
		return mData.size();
	}
	
	static class ViewHolder extends RecyclerView.ViewHolder {
		
		// Attributes card
		private final TextView titleTextView;
		private final TextView labelTextView;
		private final RecyclerView mRecyclerView;
		private final FrameLayout mFrameLayout;
		public View buttonViewOption;
		
		ViewHolder(final View view) {
			super(view);
			titleTextView = view.findViewById(R.id.home_fl_card_title_orto);
			labelTextView = view.findViewById(R.id.label_varieta);
			mRecyclerView = view.findViewById(R.id.home_fl_recycler_ortaggi);
			mFrameLayout = view.findViewById(R.id.layout_home_ortaggi);
			buttonViewOption = view.findViewById(R.id.home_fl_card_button_menu);
		}
	}
//
//	public void showPopup(View v) {
//		PopupMenu popup = new PopupMenu(context, v);
//		MenuInflater inflater = popup.getMenuInflater();
//		inflater.inflate(R.menu.home_fl_card_menu, popup.getMenu());
//		popup.show();
//	}
//
//	public void showMenu(View v) {
//		PopupMenu popup = new PopupMenu(context, v);
//
//		// This activity implements OnMenuItemClickListener
//		popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
//		popup.inflate(R.menu.home_fl_card_menu);
//		popup.show();
//	}
//
//	@Override
//	public boolean onMenuItemClick(MenuItem item) {
//		switch (item.getItemId()) {
////			case R.id.archive:
////				archive(item);
////				return true;
////			case R.id.delete:
////				delete(item);
////				return true;
//			default:
//				return false;
//		}
//	}
//
	
}
