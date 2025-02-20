package com.plantalot.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.plantalot.classes.Giardino;
import com.plantalot.classes.Orto;
import com.plantalot.components.InputDialog;
import com.plantalot.database.DbUsers;
import com.plantalot.fragments.HomeFragment;
import com.plantalot.utils.Consts;
import com.plantalot.R;

import java.util.ArrayList;

public class HomeOrtiAdapter extends RecyclerView.Adapter<HomeOrtiAdapter.ViewHolder> {

    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private final ArrayList<Orto> orti;
    private final Giardino giardino;
    private final HomeFragment homeFragment;
    private Context context;
    private String nomeOrto;
    private String error;

    public HomeOrtiAdapter(Giardino giardino, HomeFragment homeFragment) {
        this.giardino = giardino;
        this.orti = giardino.ortiList();
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.home_fl_card, viewGroup, false);
        return new ViewHolder(view);
    }


    // Bind elements to card
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Orto orto = orti.get(i);
        nomeOrto = orto.getNome();
        int specie = orto.getOrtaggi().countOrtaggi();
        int piante = orto.getOrtaggi().countPiante();

        viewHolder.titleTextView.setText(nomeOrto);
        viewHolder.specieTextView.setText(specie + " specie");
        viewHolder.pianteTextView.setText(piante + " piante");

        viewHolder.mCardView.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("nomeOrto", orto.getNome());
            Navigation.findNavController(view).navigate(R.id.action_goto_orto, bundle);
        });


        // Card popup menu
        HomeOrtiAdapter that = this;
        viewHolder.buttonViewOption.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, viewHolder.buttonViewOption);
            popup.inflate(R.menu.home_fl_card_menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popup.setGravity(Gravity.END);
            }
            popup.setOnMenuItemClickListener(item -> {

                nomeOrto = orto.getNome();

//				Log.wtf("ORTO", orto.getNome() + " - " + nomeOrto);

                switch (item.getItemId()) {  // FIXME !!!
                    case R.id.home_card_menu_opt1:

                        InputDialog inputDialog = new InputDialog(context.getString(R.string.nome_orto), nomeOrto, context);

                        inputDialog.setOnConfirm(newName -> {
                            nomeOrto = newName;
                            error = giardino.checkNomeOrto(orto.getNome(), newName, context);
                            if (error == null) {
                                giardino.editNomeOrto(orto, newName);
                                that.notifyItemChanged(i);
                                DbUsers.updateGiardino(giardino);
                            } else {
                                inputDialog.setError(error);
                                inputDialog.show();
                            }
                        });

                        inputDialog.setOnCancelListener(dialogInterface -> {
                            error = null;
                            nomeOrto = orto.getNome();
                        });

                        inputDialog.show();
                        return true;

                    case R.id.home_card_menu_opt2:
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setTitle("Eliminare " + nomeOrto + "?");
                        builder.setNegativeButton(R.string.annulla, (dialog, j) -> dialog.cancel());
                        builder.setPositiveButton(R.string.conferma, (dialog, j) -> {
                            dialog.cancel();
                            giardino.removeOrto(orto);
                            orti.remove(i);
                            this.notifyItemRemoved(i);
                            this.notifyItemRangeChanged(i, orti.size());
                            DbUsers.updateGiardino(giardino);
                            if (orti.isEmpty()) homeFragment.setupContent();
                            homeFragment.setupCard();
                        });
                        builder.show();
                        return true;

                    default:
                        return false;
                }
            });
            popup.show();
        });

        // Icons thumbnails
        // needed to get the width (fixme ?)
        if (!orto.getOrtaggi().notEmpty()) {
            viewHolder.mFrameLayout.setVisibility(View.GONE);
        } else {
            viewHolder.mFrameLayout.setVisibility(View.VISIBLE);
            viewHolder.mFrameLayout.post(() -> {
                int width = viewHolder.mFrameLayout.getWidth();
                int padding = viewHolder.mFrameLayout.getPaddingTop();
                int imgwidth = (width - 2 * padding) / (Consts.CARD_PLANTS / 2);

                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
                layoutManager.setJustifyContent(JustifyContent.CENTER);
                HomeThumbnailsAdapter homeThumbnailAdapter = new HomeThumbnailsAdapter(orto.getImages(), imgwidth);

                viewHolder.mRecyclerView.setLayoutManager(layoutManager);
                viewHolder.mRecyclerView.setAdapter(homeThumbnailAdapter);
                viewHolder.mRecyclerView.setRecycledViewPool(viewPool);
                viewHolder.mRecyclerView.suppressLayout(true);  // 🖕 (google: android propagate click to parent recyclerview)

                ViewGroup.LayoutParams params = viewHolder.mFrameLayout.getLayoutParams();
                params.height = 2 * ((width - 2 * padding) / (Consts.CARD_PLANTS / 2) + padding);
                viewHolder.mFrameLayout.setLayoutParams(params);
                if (specie == 5 || specie == 6) {  // compact view
                    viewHolder.mFrameLayout.setPadding(padding + imgwidth / 2, padding, padding + imgwidth / 2, padding);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orti.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView mCardView;
        private final TextView titleTextView;
        private final TextView specieTextView;
        private final TextView pianteTextView;
        private final RecyclerView mRecyclerView;
        private final FrameLayout mFrameLayout;
        private final View buttonViewOption;

        ViewHolder(final View view) {
            super(view);
            mCardView = view.findViewById(R.id.home_fl_card);
            titleTextView = view.findViewById(R.id.home_fl_card_title__orto);
            specieTextView = view.findViewById(R.id.home_fl_card_label__specie);
            pianteTextView = view.findViewById(R.id.home_fl_card_label__piante);
            mRecyclerView = view.findViewById(R.id.home_fl_recycler__ortaggi);
            mFrameLayout = view.findViewById(R.id.home_fl_layout__ortaggi);
            buttonViewOption = view.findViewById(R.id.home_fl_card_button_menu);
        }
    }

}