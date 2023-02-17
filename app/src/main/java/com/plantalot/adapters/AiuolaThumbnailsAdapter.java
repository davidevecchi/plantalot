package com.plantalot.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.plantalot.R;
import com.plantalot.classes.Pianta;
import com.plantalot.classes.PlantsCounter;
import com.plantalot.classes.Varieta;
import com.plantalot.components.OrtaggioSpecs;
import com.plantalot.database.DbPlants;
import com.plantalot.navigation.Nav;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// Riempe la card con le icone
@RequiresApi(api = Build.VERSION_CODES.N)
public class AiuolaThumbnailsAdapter extends RecyclerView.Adapter<AiuolaThumbnailsAdapter.ViewHolder> {
	
	private final List<String> nomiOrtaggi;
	private final View view;
	private final Context context;
	private final PlantsCounter plantsCounter;
	
	public AiuolaThumbnailsAdapter(List<String> nomiOrtaggi, PlantsCounter plantsCounter, View view) {
		this.view = view;
		this.context = view.getContext();
		this.nomiOrtaggi = nomiOrtaggi;
		this.plantsCounter = plantsCounter;
	}
	
	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.aiuola_plant_image, viewGroup, false);
		return new ViewHolder(v);
	}
	
	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
		String ortaggio = nomiOrtaggi.get(position);
		viewHolder.mImageView.setImageResource(DbPlants.getImageId(ortaggio));
		ViewGroup.LayoutParams params = viewHolder.mImageView.getLayoutParams();
		viewHolder.mImageView.setLayoutParams(params);
		viewHolder.mImageView.setOnClickListener(v -> {
			view.findViewById(R.id.orto_card_orto).setVisibility(View.GONE);
			view.findViewById(R.id.orto_card_ortaggio).setVisibility(View.VISIBLE);
			setupContentOrtaggio(ortaggio);
		});
	}
	
	@Override
	public int getItemCount() {
		return nomiOrtaggi.size();
	}
	
	static class ViewHolder extends RecyclerView.ViewHolder {
		
		ImageView mImageView;
		
		ViewHolder(View view) {
			super(view);
			mImageView = view.findViewById(R.id.aiuola_plant_image);
			
			ViewGroup.LayoutParams lp = itemView.getLayoutParams();
			if (lp instanceof FlexboxLayoutManager.LayoutParams) {
				FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams) lp;
				flexboxLp.setAlignSelf(AlignItems.BASELINE);
			}
		}
		
	}
	
	@SuppressLint("SetTextI18n")
	private void setupContentOrtaggio(String ortaggio) {
		TextView title = view.findViewById(R.id.orto_card_title);
		title.setText(ortaggio);
		
		ImageView backBtn = view.findViewById(R.id.orto_card_back_btn);
		backBtn.setOnClickListener(v -> {
			view.findViewById(R.id.orto_card_orto).setVisibility(View.VISIBLE);
			view.findViewById(R.id.orto_card_ortaggio).setVisibility(View.GONE);
		});
		
		ImageView openBtn = view.findViewById(R.id.orto_card_open_btn);
		openBtn.setOnClickListener(v -> Nav.gotoOrtaggio(ortaggio, R.id.ortoFragment, context, view));
		
		if (!plantsCounter.nomiVarieta(ortaggio).isEmpty()) {
			Varieta varietaObj = plantsCounter.getVarieta(ortaggio, plantsCounter.nomiVarieta(ortaggio).get(0));
			ImageView img = view.findViewById(R.id.orto_card_ortaggio_icon);
			img.setImageResource(DbPlants.getImageId(ortaggio));
			if (varietaObj != null) setupContentVarieta(ortaggio, varietaObj);
		}
	}
	
	@SuppressLint("SetTextI18n")
	private void setupContentVarieta(String ortaggio, Varieta varietaObj) {
		Date cDate = new Date();
		String date = new SimpleDateFormat("dd-MM-yyyy").format(cDate);  // FIXME
		
		List<OrtaggioSpecs> specs = Arrays.asList(
				new OrtaggioSpecs(
						"Distanze",
						varietaObj.getDistanze_piante() + "×" + varietaObj.getDistanze_file() + " cm",
						R.mipmap.specs_distanze_1462005,
						false),
				new OrtaggioSpecs(
						"Mezz'ombra",
						varietaObj.getAltro_tollera_mezzombra(),
						R.mipmap.specs_mezzombra_4496245,
						false),
				new OrtaggioSpecs(
						"Raccolta",
						varietaObj.getRaccolta_min() + (varietaObj.getRaccolta_max() != varietaObj.getRaccolta_min() ? "-" + varietaObj.getRaccolta_max() + " gg" : " giorni"),
						R.mipmap.specs_raccolta_3078971,
						false),
				new OrtaggioSpecs(
						"Produzione",
						varietaObj.getProduzione_peso() + " " + varietaObj.getProduzione_udm(),
						R.mipmap.specs_produzione_741366,
						false),
				new OrtaggioSpecs(
						"Rotazione",
						date,
						R.mipmap.specs_trapianto_2855575,
						false),
				new OrtaggioSpecs(
						"Vaschetta",
						plantsCounter.countPiante(ortaggio) + " piante",
						R.mipmap.specs_vaschetta_1655603,
						false)
		);
		
		RecyclerView specsRecyclerView = view.findViewById(R.id.orto_card_specs_recycler);
		specsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
		OrtaggioSpecsAdapter ortaggioSpecsAdapter = new OrtaggioSpecsAdapter(specs);
		specsRecyclerView.setAdapter(ortaggioSpecsAdapter);
		specsRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		));
	}
	
	
}
