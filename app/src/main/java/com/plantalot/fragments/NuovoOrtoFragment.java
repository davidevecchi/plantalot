package com.plantalot.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.plantalot.MyApplication;
import com.plantalot.classes.Giardino;
import com.plantalot.components.NuovoOrtoNumberSelector;
import com.plantalot.R;
import com.plantalot.adapters.NuovoOrtoOptionsAdapter;
import com.plantalot.classes.Orto;
import com.plantalot.components.AiuolaView;
import com.plantalot.database.DbUsers;
import com.plantalot.utils.DividerItemDecoration;
import com.plantalot.utils.IntPair;
import com.plantalot.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Triple;


public class NuovoOrtoFragment extends Fragment {

    private List<Triple<String, String, View>> options;
    private Orto orto, ortoNew;
    private Context context;
    private TableLayout table;
    private final IntPair tableDim = new IntPair();
    private Giardino giardino;
    private Boolean isOrto;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        giardino = ((MyApplication) this.getActivity().getApplication()).user.getGiardinoCorrente();
        isOrto = getArguments() != null;
        if (isOrto) {
            String nomeOrto = getArguments().getString("nomeOrto");
            orto = giardino.getOrti().get(nomeOrto);
            ortoNew = new Orto(orto);
        } else {
            String nomeOrtoBase = context.getResources().getString(R.string.nuovo_orto);
            String nomeOrto = nomeOrtoBase;
            int i = 0;
            while (giardino.getOrtiNames().contains(nomeOrto)) {
                nomeOrto = nomeOrtoBase + " " + ++i;
            }
            orto = ortoNew = new Orto(nomeOrto);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nuovo_orto_fragment, container, false);

        table = view.findViewById(R.id.nuovo_orto_table);
        View tableFrame = view.findViewById(R.id.nuovo_orto_table_frame);
        tableFrame.post(() -> {
            tableDim.x = tableFrame.getWidth() - Utils.dp2px(24, context);   // FIXME margin
            tableDim.y = tableFrame.getHeight() - Utils.dp2px(12, context);  // FIXME margin
            updateTable();
        });

        setupOptions();

        RecyclerView optionsRecycler = view.findViewById(R.id.nuovo_orto_options_recycler);
        optionsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        NuovoOrtoOptionsAdapter optionsAdapter = new NuovoOrtoOptionsAdapter(getActivity(), options, giardino, ortoNew, view);
        optionsRecycler.setAdapter(optionsAdapter);

        DividerItemDecoration dividerItemDecoration = new com.plantalot.utils.DividerItemDecoration(optionsRecycler.getContext(), RecyclerView.VERTICAL, false);
        optionsRecycler.addItemDecoration(dividerItemDecoration);

        Button saveBtn = view.findViewById(R.id.nuovo_orto_save_btn);
        saveBtn.setOnClickListener(v -> {
            if (isOrto) {
                giardino.editNomeOrto(orto, ortoNew.getNome());
                orto.updateParameters(ortoNew);
                DbUsers.updateGiardino(giardino);
                Bundle bundle = new Bundle();
                bundle.putString("nomeOrto", ortoNew.getNome());
                Navigation.findNavController(view).navigate(R.id.action_goto_orto, bundle);
            } else {
                giardino.addOrto(ortoNew);
                DbUsers.updateGiardino(giardino);
                Navigation.findNavController(v).navigate(R.id.action_goto_home_from_nuovoOrto);
            }
        });

        Button backBtn = view.findViewById(R.id.nuovo_orto_back_btn);
        backBtn.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        return view;
    }

    public void updateTable() {
        IntPair ortoDim = ortoNew.calcOrtoDim();
        IntPair aiuolaDim = new IntPair();

        if ((double) ortoDim.x / (double) ortoDim.y > (double) tableDim.x / (double) tableDim.y) {
            aiuolaDim.x = tableDim.x / ortoNew.getAiuoleCount().x;
            aiuolaDim.y = aiuolaDim.x * ortoNew.getAiuoleDim().y / ortoNew.getAiuoleDim().x;
        } else {
            aiuolaDim.y = tableDim.y / ortoNew.getAiuoleCount().y;
            aiuolaDim.x = aiuolaDim.y * ortoNew.getAiuoleDim().x / ortoNew.getAiuoleDim().y;
        }

        if (table.getChildAt(0) != null
                && ortoNew.getAiuoleCount().x == ((TableRow) table.getChildAt(0)).getChildCount()
                && ortoNew.getAiuoleCount().y == table.getChildCount()) {
            for (int i = 0; i < ortoNew.getAiuoleCount().y; i++) {
                TableRow row = (TableRow) table.getChildAt(i);
                for (int j = 0; j < ortoNew.getAiuoleCount().x; j++) {
                    AiuolaView ortoView = (AiuolaView) row.getChildAt(j);
                    ortoView.setSize(aiuolaDim);
                }
            }
        } else {
            table.removeAllViews();
            for (int i = 0; i < ortoNew.getAiuoleCount().y; i++) {
                TableRow row = new TableRow(context);
                for (int j = 0; j < ortoNew.getAiuoleCount().x; j++) {
                    row.addView(new AiuolaView(context, ortoNew, aiuolaDim));
                }
                table.addView(row);
            }
        }
    }

    private void setupOptions() {
        Resources res = context.getResources();
        options = new ArrayList<>(Arrays.asList(
                new Triple<>(res.getString(R.string.nome_orto), ortoNew.getNome(), null),
                new Triple<>(res.getString(R.string.dimensioni), ortoNew.getAiuoleDim().toString(),
                        new NuovoOrtoNumberSelector(
                                this, context, ortoNew.getAiuoleDim(), 40, 1000,
                                R.drawable.ic_round_spacing_horizontal_24, R.drawable.ic_round_spacing_vertical_24)),
                new Triple<>(res.getString(R.string.numero_aiuole), ortoNew.getAiuoleCount().toString(),
                        new NuovoOrtoNumberSelector(
                                this, context, ortoNew.getAiuoleCount(), 1, 8,
                                R.drawable.ic_round_table_columns_24, R.drawable.ic_round_table_rows_24)),
                new Triple<>(res.getString(R.string.esposizione), ortoNew.getEsposizione().toString(), null),
                new Triple<>(res.getString(R.string.orientamento), ortoNew.getOrientamento().toString(), null)
        ));
    }

}
