package com.plantalot.fragments;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.plantalot.R;
import com.plantalot.adapters.HomeDrawerAdapter;
import com.plantalot.classes.Giardino;
import com.plantalot.classes.User;
import com.plantalot.utils.Consts;
import com.plantalot.animations.NavigationIconClickListener;
import com.plantalot.adapters.HomeOrtiAdapter;
import com.plantalot.adapters.CircleButtonsAdapter;
import com.plantalot.viewmodels.UserModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class HomeFragment extends Fragment {

	private Giardino g;
	private User u;
	private final List<Pair<String, Integer>> mDataButtons = Arrays.asList(
			new Pair<>("Tutte le piante", R.drawable.ic_iconify_carrot_24),
			new Pair<>("Le mie piante", R.drawable.ic_iconify_sprout_24),
			new Pair<>("Visualizza carriola", R.drawable.ic_round_wheelbarrow_24),
			new Pair<>("Disponi giardino", R.drawable.ic_round_auto_24),
			new Pair<>("Aggiungi orto", R.drawable.ic_round_add_big_24));
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Collections.shuffle(Consts.plants_icons, new Random(System.currentTimeMillis()));
		final UserModel homeModel = new ViewModelProvider(this).get(UserModel.class);
		u = homeModel.getUser().getValue();
		g = u.get("Trento");
//		mDataGiardini.put("Trento", new HashMap<>());
//		mDataGiardini.put("Bergamo", new HashMap<>());
//		mDataGiardini.put("Belluno", new HashMap<>());
//
//		for (int i = 1, a = 0; i <= Consts.CARD_PLANTS + 2; i++, a += i) {
//			mDataGiardini.get("Trento").put("Orto " + i, Consts.plants_icons.subList(a, a + i));
//		}
//		mDataGiardini.get("Belluno").put("Back yard", Arrays.asList(R.mipmap.tomato_3944072, R.mipmap.cabbage_3944158, R.mipmap.garlic_3944096, R.mipmap.watermelon_3944164, R.mipmap.peanut_3944274, R.mipmap.asparagus_3944087, R.mipmap.beet_3944102, R.mipmap.chard_3944149, R.mipmap.broccoli_3944323, R.mipmap.artichoke_3944084, R.mipmap.melon_3944173, R.mipmap.carrot_3944093, R.mipmap.cauliflower_3944060, R.mipmap.cabbage_3944158, R.mipmap.kale_3944155, R.mipmap.cucumber_3944113, R.mipmap.onion_3944225, R.mipmap.beans_3944104, R.mipmap.soybean_3944107, R.mipmap.fennel_3944161, R.mipmap.strawberry_3944176, R.mipmap.lettuce_3944125, R.mipmap.corn_3944286, R.mipmap.eggplants_3944110, R.mipmap.melon_3944173, R.mipmap.chili_pepper_3944137, R.mipmap.pepper_3944075, R.mipmap.leek_3944259, R.mipmap.vegetable_3944143, R.mipmap.turnip_3944078, R.mipmap.shallot_3944099, R.mipmap.celery_3944146, R.mipmap.vegetable_3944152, R.mipmap.spinach_3944292, R.mipmap.melon_3944173, R.mipmap.pumpkin_3944344, R.mipmap.zucchini_3944064));
//		mDataGiardini.get("Bergamo").put("Dietro casa", Arrays.asList(R.mipmap.cucumber_3944113, R.mipmap.onion_3944225, R.mipmap.beans_3944104, R.mipmap.fennel_3944161, R.mipmap.strawberry_3944176, R.mipmap.lettuce_3944125, R.mipmap.corn_3944286, R.mipmap.eggplants_3944110, R.mipmap.melon_3944173, R.mipmap.chili_pepper_3944137, R.mipmap.pepper_3944075, R.mipmap.leek_3944259, R.mipmap.vegetable_3944143, R.mipmap.turnip_3944078, R.mipmap.shallot_3944099, R.mipmap.celery_3944146, R.mipmap.vegetable_3944152, R.mipmap.spinach_3944292, R.mipmap.melon_3944173, R.mipmap.pumpkin_3944344, R.mipmap.zucchini_3944064));
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_fragment, container, false);
		setUpRecyclerView(view);
		setUpToolbar(view);
		return view;
	}
	
	private void setUpRecyclerView(@NonNull View view) {
		System.out.println("Dimensione utente : " + u.size());

		String key = "Trento";
		TextView title = view.findViewById(R.id.home_fl_title_giardino);
		title.setText(key);
		
		RecyclerView giardiniRecyclerView = view.findViewById(R.id.home_bl_drawer_recycler);
		giardiniRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

		// FIXME
		List<String> giardini = u.getGiardini();
		if(giardini.size()>0){
			HomeDrawerAdapter giardiniAdapter = new HomeDrawerAdapter(getActivity(), giardini);
			giardiniRecyclerView.setAdapter(giardiniAdapter);
		}


		RecyclerView ortiRecyclerView = view.findViewById(R.id.home_fl_recycler_orti);
		ortiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		System.out.println(g);
		HomeOrtiAdapter homeOrtiAdapter = new HomeOrtiAdapter(g);
		ortiRecyclerView.setAdapter(homeOrtiAdapter);

		RecyclerView navbuttonsRecyclerView = view.findViewById(R.id.home_fl_recycler_navbuttons);
		CircleButtonsAdapter circleButtonsAdapter = new CircleButtonsAdapter(mDataButtons);
		FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getContext());
		flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
		navbuttonsRecyclerView.setLayoutManager(flexboxLayoutManager);
		navbuttonsRecyclerView.setAdapter(circleButtonsAdapter);
	}
	
	private void setUpToolbar(@NonNull View view) {
		Toolbar toolbar = view.findViewById(R.id.home_bl_toolbar);
		AppCompatActivity activity = (AppCompatActivity) getActivity();
		
		if (activity != null) {
			activity.setSupportActionBar(toolbar);
		}
		
		final LinearLayout drawer = view.findViewById(R.id.home_bl_drawer);
		drawer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		
		// animation
		toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
				getContext(),
				view.findViewById(R.id.home_backdrop_frontlayer),
				new AccelerateDecelerateInterpolator(),
				R.drawable.ic_round_menu_24,
				R.drawable.ic_round_close_24,
				drawer.getMeasuredHeight()));
	}
	
	// Show appbar right menu
	@Override
	public void onPrepareOptionsMenu(@NonNull final Menu menu) {
		getActivity().getMenuInflater().inflate(R.menu.home_bl_toolbar_menu, menu);
	}
	
}
