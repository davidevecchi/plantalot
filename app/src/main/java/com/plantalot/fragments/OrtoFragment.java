package com.plantalot.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.plantalot.MyApplication;
import com.plantalot.R;
import com.plantalot.adapters.AiuolaThumbnailsAdapter;
import com.plantalot.classes.PlantsCounter;
import com.plantalot.classes.Giardino;
import com.plantalot.classes.Orto;
import com.plantalot.components.AiuolaView;
import com.plantalot.components.PieChartView;
import com.plantalot.database.DbUsers;
import com.plantalot.utils.IntPair;
import com.plantalot.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class OrtoFragment extends Fragment {

    private Orto orto;
    private Context context;
    private TableLayout table;
    private final IntPair tableDim = new IntPair();
    private Giardino giardino;
    private View view;
    private String nomeOrto;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private PlantsCounter ortaggi;
    private PieChartView mPieChart;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nomeOrto = getArguments().getString("nomeOrto");
        context = getContext();
        giardino = ((MyApplication) this.getActivity().getApplication()).user.getGiardinoCorrente();
        orto = giardino.getOrti().get(nomeOrto);
        ortaggi = orto.getOrtaggi();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {  // FIXME
        super.onResume();
        giardino = ((MyApplication) this.getActivity().getApplication()).user.getGiardinoCorrente();
        orto = giardino.getOrti().get(nomeOrto);
        ortaggi = orto.getOrtaggi();
        updatePieChart();
        TextView textView = view.findViewById(R.id.orto_card_orto_text);
        if (orto.isEmpty()) {
            textView.setText("\n\n" + getString(R.string.orto_vuoto));
            textView.setGravity(Gravity.CENTER);
        } else {
            textView.setText(R.string.questo_orto);
            textView.setGravity(Gravity.NO_GRAVITY);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.orto_fragment, container, false);

        ((TextView) view.findViewById(R.id.orto_appbar_title)).setText(orto.getNome());

        table = view.findViewById(R.id.orto_table);
        View tableFrame = view.findViewById(R.id.orto_table_frame);
        tableFrame.post(() -> {
            tableDim.x = tableFrame.getWidth() - Utils.dp2px(24, context);   // FIXME margin
            tableDim.y = tableFrame.getHeight() - Utils.dp2px(12, context);  // FIXME margin
            updateTable();
        });

        view.findViewById(R.id.orto_card_orto).setVisibility(View.VISIBLE);
        view.findViewById(R.id.orto_card_ortaggio).setVisibility(View.GONE);

        mPieChart = view.findViewById(R.id.piechart_view);
        updatePieChart();

        Button backBtn = view.findViewById(R.id.orto_back_btn);
        backBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_goto_home_from_orto));

        Button editBtn = view.findViewById(R.id.orto_edit_btn);
        editBtn.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, editBtn);
            popup.inflate(R.menu.orto_menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.setGravity(Gravity.END);
            }
            popup.setOnMenuItemClickListener(item -> {

                Bundle bundle = new Bundle();
                bundle.putString("nomeOrto", orto.getNome());

                switch (item.getItemId()) {  // FIXME !!!
                    case R.id.orto_menu_opt1:
                        Navigation.findNavController(view).navigate(R.id.action_goto_nuovo_orto, bundle);
                        return true;

                    case R.id.orto_menu_opt2:
                        Navigation.findNavController(view).navigate(R.id.action_goto_carriola, bundle);
                        return true;

                    case R.id.orto_menu_opt3:
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setTitle("Eliminare " + nomeOrto + "?");
                        builder.setNegativeButton(R.string.annulla, (dialog, j) -> dialog.cancel());
                        builder.setPositiveButton(R.string.conferma, (dialog, j) -> {
                            dialog.cancel();
                            giardino.removeOrto(orto);
                            DbUsers.updateGiardino(giardino);
                            Navigation.findNavController(view).navigate(R.id.action_goto_home_from_orto);
                        });
                        builder.show();
                        return true;

                    default:
                        return false;
                }
            });
            popup.show();
        });

        setupBackButtonHandler();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateTable() {
        IntPair ortoDim = orto.calcOrtoDim();
        IntPair aiuolaDim = new IntPair();

        if ((double) ortoDim.x / (double) ortoDim.y > (double) tableDim.x / (double) tableDim.y) {
            aiuolaDim.x = tableDim.x / orto.getAiuoleCount().x;
            aiuolaDim.y = aiuolaDim.x * orto.getAiuoleDim().y / orto.getAiuoleDim().x;
        } else {
            aiuolaDim.y = tableDim.y / orto.getAiuoleCount().y;
            aiuolaDim.x = aiuolaDim.y * orto.getAiuoleDim().x / orto.getAiuoleDim().y;
        }

        int flexDirection = aiuolaDim.x > aiuolaDim.y ? FlexDirection.ROW : FlexDirection.COLUMN;
        ArrayList<String> nomiOrtaggi = orto.getOrtaggi().nomiOrtaggi();
        int quotient = nomiOrtaggi.size() / (orto.getAiuoleCount().y * orto.getAiuoleCount().x);
        int remainder = nomiOrtaggi.size() % (orto.getAiuoleCount().y * orto.getAiuoleCount().x);
        int start = 0;

        for (int y = 0; y < orto.getAiuoleCount().y; y++) {
            TableRow row = new TableRow(context);
            for (int x = 0; x < orto.getAiuoleCount().x; x++) {
                AiuolaView aiuolaView = new AiuolaView(context, orto, aiuolaDim);
                row.addView(aiuolaView);

                if (start < nomiOrtaggi.size()) {

                    int end = start + quotient + (--remainder >= 0 ? 1 : 0);
                    List<String> ortaggi = nomiOrtaggi.subList(start, end);
                    start = end;

                    FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context, flexDirection);
                    layoutManager.setJustifyContent(JustifyContent.CENTER);
                    layoutManager.setFlexWrap(FlexWrap.NOWRAP);
                    layoutManager.setAlignItems(AlignItems.STRETCH);
                    AiuolaThumbnailsAdapter aiuolaThumbnailAdapter = new AiuolaThumbnailsAdapter(ortaggi, orto.getOrtaggi(), view);

                    RecyclerView mRecyclerView = aiuolaView.findViewById(R.id.component_aiuola_content);
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(aiuolaThumbnailAdapter);
                    mRecyclerView.setRecycledViewPool(viewPool);
                }
            }
            table.addView(row);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updatePieChart() {
        mPieChart.updateContent(ortaggi);
    }


    private void setupBackButtonHandler() {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                Navigation.findNavController(v).navigate(R.id.action_goto_home_from_orto);
                return true;
            }
            return false;
        });
    }

}
