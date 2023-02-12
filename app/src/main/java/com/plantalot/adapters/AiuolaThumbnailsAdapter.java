package com.plantalot.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.plantalot.R;
import com.plantalot.classes.Pianta;
import com.plantalot.classes.Varieta;
import com.plantalot.components.OrtaggioSpecs;
import com.plantalot.database.DbPlants;
import com.plantalot.navigation.Nav;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// Riempe la card con le icone
public class AiuolaThumbnailsAdapter extends RecyclerView.Adapter<AiuolaThumbnailsAdapter.ViewHolder> {

    private final Map<String, Varieta> ortaggioDocuments = new HashMap<>();

    private LinkedList<String> dropdownItems;
    private final List<String> nomiOrtaggi;
    View view;
    Context context;

    public AiuolaThumbnailsAdapter(@NonNull List<String> nomiOrtaggi, View view) {
        this.nomiOrtaggi = nomiOrtaggi;
        this.view = view;
        this.context = view.getContext();
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
    private void setupContentOrto() {

        ImageView img = view.findViewById(R.id.orto_card_ortaggio_icon);

    }


    @SuppressLint("SetTextI18n")
    private void setupContentOrtaggio(String ortaggio) {
        final String GENERICO = "Generico";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView title = view.findViewById(R.id.orto_card_title);
        title.setText(ortaggio);

        ImageView backBtn = view.findViewById(R.id.orto_card_back_btn);
        backBtn.setOnClickListener(v -> {
            view.findViewById(R.id.orto_card_orto).setVisibility(View.VISIBLE);
            view.findViewById(R.id.orto_card_ortaggio).setVisibility(View.GONE);
        });

        ImageView openBtn = view.findViewById(R.id.orto_card_open_btn);
        openBtn.setOnClickListener(v -> Nav.gotoOrtaggio(ortaggio, R.id.ortoFragment, context, view));

        db.collection("varieta")
                .whereEqualTo(DbPlants.VARIETA_CLASSIFICAZIONE_ORTAGGIO, ortaggio)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ortaggioDocuments.put(doc.get(DbPlants.VARIETA_CLASSIFICAZIONE_VARIETA).toString(), doc.toObject(Varieta.class));
                    }

                    String defaultVarieta = ortaggioDocuments.size() == 1
                            ? new ArrayList<>(ortaggioDocuments.keySet()).get(0)
                            : "Generico";
                    Varieta varieta = ortaggioDocuments.get(defaultVarieta);

                    dropdownItems = new LinkedList<>(ortaggioDocuments.keySet());
                    Collections.sort(dropdownItems);
                    if (dropdownItems.contains(GENERICO)) {
                        dropdownItems.remove(GENERICO);
                        dropdownItems.add(0, GENERICO);
                    }

//                    db.collection("icons")
//                            .document(varieta.getClassificazione_ortaggio())
//                            .get().addOnSuccessListener(document -> {
//                                if (!document.exists()) return;
                    ImageView img = view.findViewById(R.id.orto_card_ortaggio_icon);
                    img.setImageResource(DbPlants.getImageId(ortaggio));
//                            });

                    db.collection("piante")
                            .document(varieta.getClassificazione_pianta())
                            .get().addOnSuccessListener(document -> {
                                if (!document.exists()) return;
                                Pianta pianta = document.toObject(Pianta.class);
                                setupContentVarieta(pianta, varieta);
                            });
                });
    }

    @SuppressLint("SetTextI18n")
    private void setupContentVarieta(Pianta pianta, Varieta varieta) {
        // Specs
        List<OrtaggioSpecs> specs = Arrays.asList(
                new OrtaggioSpecs(
                        "Distanze",
                        varieta.getDistanze_piante() + "×" + varieta.getDistanze_file() + " cm",
                        R.mipmap.specs_distanze_1462005,
                        false),
                new OrtaggioSpecs(
                        "Mezz'ombra",
                        varieta.getAltro_tollera_mezzombra(),
                        R.mipmap.specs_mezzombra_4496245,
                        false),
                new OrtaggioSpecs(
                        "Raccolta",
                        varieta.getRaccolta_min() + (varieta.getRaccolta_max() != varieta.getRaccolta_min() ? "-" + varieta.getRaccolta_max() + " gg" : " giorni"),
                        R.mipmap.specs_raccolta_3078971,
                        false),
                new OrtaggioSpecs(
                        "Produzione",
                        varieta.getProduzione_peso() + " " + varieta.getProduzione_udm(),
                        R.mipmap.specs_produzione_741366,
                        false),
                new OrtaggioSpecs(
                        "Rotazione",
                        pianta.getRotazioni_anni() + " anni",
                        R.mipmap.specs_rotazione_4496256,
                        false),
                new OrtaggioSpecs(
                        "Vaschetta",
                        varieta.getAltro_pack() + " piante",
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
