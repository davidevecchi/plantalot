package com.plantalot.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.plantalot.components.PieChartView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// FIXME spostare i caricamenti dal DB in Splash.java

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private View view;
    private MyApplication app;
    private boolean doubleBackToExitPressedOnce = false;
    private PieChartView mPieChart;
    private Giardino giardino;

    private static final List<Pair<CircleButton, Boolean>> mButtons = new ArrayList<>(Arrays.asList(new Pair<>(new CircleButton("Tutte le piante", R.drawable.ic_iconify_carrot_24, /*R.id.*/ "action_goto_all_plants"), true), new Pair<>(new CircleButton("Visualizza carriola", R.drawable.ic_round_wheelbarrow_24, /*R.id.*/ "action_goto_carriola"), true), new Pair<>(new CircleButton("Aggiungi orto", R.drawable.ic_round_add_big_24, /*R.id.*/ "action_goto_nuovo_orto"), true), new Pair<>(new CircleButton("Le mie piante", R.drawable.ic_iconify_sprout_24), false), new Pair<>(new CircleButton("Guarda carriola", R.drawable.ic_round_wheelbarrow_24), false), new Pair<>(new CircleButton("Disponi giardino", R.drawable.ic_round_auto_24), false)));

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On create");
        super.onCreate(savedInstanceState);
        app = (MyApplication) this.getActivity().getApplication();
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "On createView");
        view = inflater.inflate(R.layout.home_fragment, container, false);
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (!doubleBackToExitPressedOnce) {
                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(getContext(), R.string.exit_toast, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                } else {
                    getParentFragmentManager().popBackStack();
                }
                return true;
            }
            return false;
        });
        new Handler().post(this::setupContent);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public /* FIXME */ void setupContent() {
        if (app.user == null) return;
        Log.d(TAG, "Updating user " + app.user.getUsername());

        TextView instructions = view.findViewById(R.id.instructions);
        TextView title = view.findViewById(R.id.home_fl_title_giardino);
        ImageView background = view.findViewById(R.id.home_fl_background);
        FrameLayout chartCard = view.findViewById(R.id.home_chart);
        view.findViewById(R.id.cardViewGraph).setBackgroundColor(0xfff6f6e9);

        title.setVisibility(View.VISIBLE); // FIXME
        instructions.setVisibility(View.VISIBLE); // FIXME
        chartCard.setVisibility(View.GONE); // FIXME

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        boolean higherThanMinHeight = displayMetrics.heightPixels > 2000;

        giardino = app.user.getGiardinoCorrente();
        if (giardino == null) {
            giardino = new Giardino("nomeGiardino", "posizioneGiardino");
            app.user.addGiardino(giardino);
        }
        if (giardino.getOrti().isEmpty()) {
            instructions.setText(R.string.instruction_no_orti);
            if (higherThanMinHeight) background.setVisibility(View.VISIBLE); // FIXME
        } else {
            instructions.setVisibility(View.GONE); // FIXME
            background.setVisibility(View.GONE); // FIXME
            chartCard.setVisibility(View.VISIBLE); // FIXME
            mPieChart = view.findViewById(R.id.piechart_view);
            Handler handler = new Handler();
            handler.postDelayed(this::setupCard, 100);  // fixme !!!
            handler.postDelayed(this::setupCard, 500);  // fixme !!!
        }

        List<CircleButton> buttonList = new ArrayList<>();
        for (Pair<CircleButton, Boolean> button : mButtons) {
            if (button.second) buttonList.add(button.first);
        }
        CircleButton.setupRecycler(buttonList, view.findViewById(R.id.home_fl_recycler_navbuttons), view.getContext(), giardino.getCarriola().countOrtaggi());

        RecyclerView ortiRecyclerView = view.findViewById(R.id.home_fl_recycler_orti);
        ortiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HomeOrtiAdapter homeOrtiAdapter = new HomeOrtiAdapter(giardino, this);
        ortiRecyclerView.setAdapter(homeOrtiAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setupCard() {
        mPieChart.update(giardino.getFamiglieCount());
        int totalArea = giardino.calcArea();
        int plantedArea = giardino.plantedArea();
        int carriolaArea = giardino.getCarriola().calcArea();
        int freeArea = totalArea - (plantedArea + carriolaArea);
        String text = "" + CarriolaFragment.format(totalArea) + "\n" + CarriolaFragment.format(plantedArea) + "\n" + CarriolaFragment.format(carriolaArea) + "\n" + CarriolaFragment.format(freeArea);
        ((TextView) view.findViewById(R.id.carriola_area_values)).setText(text);
    }


}
