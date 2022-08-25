package com.plantalot.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.plantalot.MyApplication;
import com.plantalot.R;
import com.plantalot.adapters.HomeGiardiniAdapter;
import com.plantalot.classes.Giardino;
import com.plantalot.animations.NavigationIconClickListener;
import com.plantalot.adapters.HomeOrtiAdapter;
import com.plantalot.components.CircleButton;
import com.plantalot.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// FIXME spostare i caricamenti dal DB in Splash.java

public class HomeFragment extends Fragment {
	
	private static final String TAG = "HomeFragment";
	private String nomeGiardinoCorrente;
	private View view;
	private MyApplication app;
	
	private static final List<Pair<CircleButton, Boolean>> mButtons = new ArrayList<>(Arrays.asList(
			new Pair<>(new CircleButton("Tutte le piante", R.drawable.ic_iconify_carrot_24, R.id.action_goto_all_plants), true),
			new Pair<>(new CircleButton("Visualizza carriola", R.drawable.ic_round_wheelbarrow_24, R.id.action_goto_carriola), true),
			new Pair<>(new CircleButton("Le mie piante", R.drawable.ic_iconify_sprout_24), false),
			new Pair<>(new CircleButton("Guarda carriola", R.drawable.ic_round_wheelbarrow_24), false),
			new Pair<>(new CircleButton("Disponi giardino", R.drawable.ic_round_auto_24), false),
			new Pair<>(new CircleButton("Aggiungi orto", R.drawable.ic_round_add_big_24, R.id.action_goto_nuovo_orto), true)
	));
	
	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(Consts.KEY_GIARDINO, nomeGiardinoCorrente);
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null) {
			nomeGiardinoCorrente = savedInstanceState.getString(Consts.KEY_GIARDINO);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "On create");
		super.onCreate(savedInstanceState);
		app = (MyApplication) this.getActivity().getApplication();
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "On createView");
		view = inflater.inflate(R.layout.home_fragment, container, false);
		new Handler().post(this::setupUI);
//		view.findViewById(R.id.home_bl_drawer_recycler).setOnClickListener(v -> setupContent());
		return view;
	}
	
	// Show appbar right menu
	@Override
	public void onPrepareOptionsMenu(@NonNull final Menu menu) {
		getActivity().getMenuInflater().inflate(R.menu.home_bl_toolbar_menu, menu);
	}
	
	private void setupUI() {
		setUpToolbar();
		
		// Add link to new_garden fragment
		Button new_garden = view.findViewById(R.id.nuovo_giardino);
		new_garden.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_goto_nuovo_giardino));
		
		if (app.user == null) return;
		Log.d(TAG, "------------------------------ Updating user " + app.user.getUsername());
		
		// Setup giardini recycler view
		RecyclerView giardiniRecyclerView = view.findViewById(R.id.home_bl_drawer_recycler);
		giardiniRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		giardiniRecyclerView.setAdapter(new HomeGiardiniAdapter(view.getContext(), app.user.getGiardiniNames(), app.user.getNome_giardino_corrente(), view, app, this));
		
		setupContent();
	}
	
	public /* FIXME */ void setupContent() {
		TextView instructions = view.findViewById(R.id.instructions);
		TextView title = view.findViewById(R.id.home_fl_title_giardino);
		
		title.setVisibility(View.VISIBLE); // FIXME
		instructions.setVisibility(View.VISIBLE); // FIXME
		
		nomeGiardinoCorrente = app.user.getNome_giardino_corrente();
//		Log.d("Giardino corrente", nomeGiardinoCorrente);
		
		if (nomeGiardinoCorrente == null) {
			instructions.setText(R.string.instruction_no_giardini);
			title.setVisibility(View.GONE); // FIXME
		} else {
			Giardino giardino = app.user.getGiardinoCorrente();
			if (giardino.getOrti().isEmpty()) {
				instructions.setText(R.string.instruction_no_orti);
			} else {
				instructions.setVisibility(View.GONE); // FIXME
			}
			List<CircleButton> buttonList = new ArrayList<>();
			for (Pair<CircleButton, Boolean> button : mButtons) {
				if (button.second) buttonList.add(button.first);
			}
			CircleButton.setupRecycler(buttonList, view.findViewById(R.id.home_fl_recycler_navbuttons), view.getContext());
			title.setText(nomeGiardinoCorrente);
			
			// Setup orti recycler view
			RecyclerView ortiRecyclerView = view.findViewById(R.id.home_fl_recycler_orti);
			ortiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
			HomeOrtiAdapter homeOrtiAdapter = new HomeOrtiAdapter(giardino);
			ortiRecyclerView.setAdapter(homeOrtiAdapter);
		}
	}
	
	private void setUpToolbar() {
		Toolbar toolbar = view.findViewById(R.id.home_bl_toolbar);
		AppCompatActivity activity = (AppCompatActivity) getActivity();
		
		if (activity != null) activity.setSupportActionBar(toolbar);
		
		// Setup listener + animation
		toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
				getContext(),
				view.findViewById(R.id.home_backdrop_frontlayer),
				new AccelerateDecelerateInterpolator(),
				R.drawable.ic_round_menu_24,
				R.drawable.ic_round_close_24,
				view.findViewById(R.id.home_bl_drawer)
		));
	}
	
}
