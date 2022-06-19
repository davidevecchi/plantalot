package com.plantalot.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.plantalot.R;
import com.plantalot.components.CircleButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class CircleButtonsAdapter extends RecyclerView.Adapter<CircleButtonsAdapter.ViewHolder> {
	
	private final List<CircleButton> mData;
	
	// FIXME
	private List<String> ortaggi_list = Arrays.asList("Aglio", "Anguria", "Arachide", "Barbabietola", "Basilico", "Bietola", "Broccolo", "Carosello", "Carota", "Catalogna", "Cavolfiore", "Cavolo cappuccio", "Cavolo cinese", "Cavolo di Bruxelles", "Cavolo nero", "Cavolo riccio", "Cece", "Cetriolo", "Cicoria", "Cima di rapa", "Cipolla", "Cipollotto", "Erba cipollina", "Fagiolino", "Fagiolo", "Fava", "Finocchio", "Indivia", "Lattuga", "Mais", "Melanzana", "Melone", "Okra gombo", "Peperoncino", "Peperone", "Pisello", "Pomodoro", "Porro", "Prezzemolo", "Puntarelle", "Radicchio", "Rapa", "Ravanello", "Rucola", "Scalogno", "Sedano", "Sedano rapa", "Spinacio", "Valeriana", "Verza", "Zucca", "Zucchino");
	
	public CircleButtonsAdapter(List<CircleButton> data) {
		this.mData = data;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.component_circle_button, viewGroup, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
		int icon = mData.get(position).getIcon();
		String label = mData.get(position).getLabel();
		int id_fragment = mData.get(position).getId_fragment();
		Bundle bundle = mData.get(position).getBundle();

		viewHolder.mButton.setIconResource(icon);
		viewHolder.mTextView.setText(label);
		ViewGroup.LayoutParams params = viewHolder.mTextView.getLayoutParams();
		viewHolder.mTextView.setLayoutParams(params);
		if(id_fragment != -1)
			viewHolder.mCard.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {  // FIXME !!!! [ Max trova la best practice per collegare un'azione diversa ad ogni bottone, che non sia necessariamente di navigazione ]
					Navigation.findNavController(view).navigate(id_fragment, bundle);
				}
			});
	}
	
	@Override
	public int getItemCount() {
		return mData.size();
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder {
		MaterialCardView mCard;
		MaterialButton mButton;
		TextView mTextView;
		
		public ViewHolder(View view) {
			super(view);
			mCard = view.findViewById(R.id.component_circle_button);
			mButton = view.findViewById(R.id.component_circle_button_icon);
			mTextView = view.findViewById(R.id.component_circle_button_label);
			ViewGroup.LayoutParams lp = itemView.getLayoutParams();
			if (lp instanceof FlexboxLayoutManager.LayoutParams) {
				FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
				flexboxLp.setAlignSelf(AlignItems.CENTER);
			}
		}
	}
	
}
