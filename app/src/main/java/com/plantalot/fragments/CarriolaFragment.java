package com.plantalot.fragments;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.plantalot.MyApplication;
import com.plantalot.R;
import com.plantalot.adapters.CarriolaOrtaggiAdapter;
import com.plantalot.adapters.ChartLegendAdapter;
import com.plantalot.classes.Carriola;
import com.plantalot.classes.Giardino;
import com.plantalot.classes.Orto;
import com.plantalot.classes.Varieta;
import com.plantalot.components.PieChartView;
import com.plantalot.database.DbUsers;
import com.plantalot.utils.ColorUtils;
import com.plantalot.utils.Consts;

//import org.eazegraph.lib.charts.PieChart;
//import org.eazegraph.lib.models.PieModel;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


public class CarriolaFragment extends Fragment {

    private View view;
    private Giardino giardino;
    private Carriola carriola, carriolaNew;
    private int totalArea, plantedArea, carriolaArea;
    private TextView areaValuesTv;
    private Button confirmBtn, clearBtn;
    private LinearLayout buttons;
    private Orto orto = null;
    private Boolean isOrto;
    private HashSet<String> ortiSet;
    private NestedScrollView scrollView;
    private PieChartView mPieChart;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        giardino = ((MyApplication) this.getActivity().getApplication()).user.getGiardinoCorrente();
        isOrto = getArguments() != null;
        if (isOrto) {
            String nomeOrto = getArguments().getString("nomeOrto");
            orto = giardino.getOrti().get(nomeOrto);
            carriola = orto.getOrtaggi();
            carriolaNew = new Carriola(carriola);
        } else {
            carriola = carriolaNew = giardino.getCarriola();
            carriola.removeEmpty();
            ortiSet = new HashSet<>(giardino.getOrtiNames());
        }
        totalArea = giardino.calcArea();
        plantedArea = giardino.plantedArea();
        carriolaArea = giardino.getCarriola().calcArea();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.carriola_fragment, container, false);
        areaValuesTv = view.findViewById(R.id.carriola_area_values);
        confirmBtn = view.findViewById(R.id.carriola_confirm_btn);
        clearBtn = view.findViewById(R.id.carriola_clear_btn);
        buttons = view.findViewById(R.id.carriola_buttons);

        if (isOrto) {
            MaterialToolbar toolbar = view.findViewById(R.id.carriola_toolbar);
            toolbar.setTitle(orto.getNome());
            confirmBtn.setText("Conferma");
            clearBtn.setText("Rimuovi tutti gli ortaggi");
        } else {
            setCofirmBtnText();
            if (giardino.getOrti().size() > 1) {
                view.findViewById(R.id.carriola_chips_scroll_view).setVisibility(View.VISIBLE);
                setupChips();
            }
        }

        setupToolbar();
        updateOccupiedArea();
        setupChart();

        if (carriola.notEmpty()) {
            buttons.setVisibility(View.VISIBLE);
            view.findViewById(R.id.carriola_progressBar).setVisibility(View.VISIBLE);
            view.findViewById(R.id.carriola_text_vuota).setVisibility(View.GONE);
            setupContent(carriola.toList());
        } else {
            buttons.setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.carriola_text_vuota)).setText(isOrto ? R.string.orto_vuoto : R.string.carriola_vuota);
        }
        return view;
    }

    private void setupChips() {
        ChipGroup chipGroup = view.findViewById(R.id.carriola_chip_group);

        for (String item : giardino.getOrtiNames()) {
            Chip chip = new Chip(getContext());
            chip.setText(item);
            chip.setCheckable(true);
            chip.setChecked(true);
            chip.setCheckedIconVisible(true);
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_round_done_24);
            drawable.setTint(chip.getCurrentTextColor());
            chip.setCheckedIcon(drawable);

            chip.setOnCheckedChangeListener((compoundButton, b) -> {
                boolean checked = chip.isChecked();
                if (checked) {
                    ortiSet.add((String) chip.getText());
                } else {
                    ortiSet.remove((String) chip.getText());
                }
                setCofirmBtnText();
            });

            chipGroup.addView(chip);
        }
    }

    private void setCofirmBtnText() {
        String pianta_in = "Pianta in ";
        if (ortiSet.size() == 1) {
            pianta_in += ortiSet.iterator().next();
        } else if (ortiSet.size() == giardino.ortiList().size()) {
            pianta_in += "tutti gli orti";
        } else {
            pianta_in += ortiSet.size() + " orti";
        }
        confirmBtn.setText(pianta_in);
    }

    public static String format(int area) {
        return (area < 0 ? "-" : "")
                + Math.abs(area / 10000) + ","
                + Math.abs(area / 1000 - 10 * (area / 10000)) + " m²";
    }

    public void updateOccupiedArea() {
        updateOccupiedArea(0);
    }

    public void updateOccupiedArea(int updateArea) {
        carriolaArea += updateArea;
        int freeArea = totalArea - (plantedArea + carriolaArea);
        String text = ""
                + format(totalArea) + "\n"
                + format(plantedArea) + "\n"
                + format(carriolaArea) + "\n"
                + format(freeArea);
        areaValuesTv.setText(text);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupContent(List<Pair<String, List<Pair<Varieta, Integer>>>> carriolaList) {
        Handler handler = new Handler();
        handler.post(() -> {
            view.findViewById(R.id.carriola_progressBar).setVisibility(View.GONE);
            RecyclerView ortaggiRecyclerView = view.findViewById(R.id.carriola_ortaggi_recycler);
            ortaggiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            CarriolaOrtaggiAdapter carriolaOrtaggiAdapter = new CarriolaOrtaggiAdapter(carriolaList, carriolaNew, giardino, isOrto, this);
            ortaggiRecyclerView.setAdapter(carriolaOrtaggiAdapter);
            confirmBtn.setEnabled(giardino.getOrti().size() > 0);
        });

        confirmBtn.setOnClickListener(v -> {
            if (isOrto) {
                updateOrto();
            } else {
                arrangeOrtaggi();
            }
            DbUsers.updateGiardino(giardino);
            Navigation.findNavController(view).popBackStack();
        });

        clearBtn.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle("Rimuovere tutti gli ortaggi?");
            builder.setNegativeButton(R.string.annulla, (dialog, j) -> dialog.cancel());
            builder.setPositiveButton(R.string.conferma, (dialog, j) -> {
                dialog.cancel();
                carriola.clear();
                DbUsers.updateGiardino(giardino);
                Navigation.findNavController(view).popBackStack();
            });
            builder.show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupChart() {
        mPieChart = view.findViewById(R.id.piechart_view);
        scrollView = view.findViewById(R.id.carriola_scrollview);
        updatePieChart();

        ViewTreeObserver.OnScrollChangedListener scrollListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (isViewVisible(mPieChart)) {
                    mPieChart.startAnimation();
                    scrollView.getViewTreeObserver().removeOnScrollChangedListener(this);
                }
            }
        };
        scrollView.getViewTreeObserver().addOnScrollChangedListener(scrollListener);
    }

    private boolean isViewVisible(View view) {
        Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        return view.getLocalVisibleRect(scrollBounds);
    }

    private void arrangeOrtaggi() {  // TODO
        Random rnd = new Random();
        HashMap<String, Orto> orti = giardino.getOrti();
        ArrayList<String> keys = new ArrayList<>(ortiSet);
        for (String ortaggio : carriolaNew.nomiOrtaggi()) {
            for (String varieta : carriolaNew.nomiVarieta(ortaggio)) {
                int count = carriolaNew.getPianteCount(ortaggio, varieta);
                if (count > 0) {
                    int r = rnd.nextInt(keys.size());
                    orti.get(keys.get(r)).addVarieta(ortaggio, varieta, count);
                }
            }
        }
        giardino.setOrti(orti);
        carriola.clear();
        carriolaNew.clear();
        giardino.setCarriola(carriola);
    }

    private void updateOrto() {
        carriolaNew.removeEmpty();
        orto.setOrtaggi(carriolaNew);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = view.findViewById(R.id.carriola_toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (!isOrto) {
                carriolaNew.removeEmpty();
                giardino.setCarriola(carriolaNew);
                DbUsers.updateGiardino(giardino);
            }
            Navigation.findNavController(v).popBackStack();
        });
        view.findViewById(R.id.carriola_help).setOnClickListener(menuItem -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
            builder.setTitle(R.string.carriola);
            builder.setMessage(R.string.carriola_help);
            builder.setPositiveButton(R.string.capito, (dialog, i) -> dialog.cancel());
            builder.show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updatePieChart() {
        mPieChart.update(carriolaNew.getFamiglieCount());
    }

}
