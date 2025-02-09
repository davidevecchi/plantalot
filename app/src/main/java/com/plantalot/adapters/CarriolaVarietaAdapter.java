package com.plantalot.adapters;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.plantalot.R;
import com.plantalot.classes.PlantsCounter;
import com.plantalot.classes.Giardino;
import com.plantalot.classes.Varieta;
import com.plantalot.database.DbPlants;
import com.plantalot.database.DbUsers;
import com.plantalot.fragments.CarriolaFragment;
import com.plantalot.utils.ColorUtils;

import java.util.List;
import java.util.Locale;

public class CarriolaVarietaAdapter extends RecyclerView.Adapter<CarriolaVarietaAdapter.ViewHolder> {

    private final List<Pair<Varieta, Integer>> mData;
    private final CarriolaOrtaggiAdapter mParentAdapter;
    private final Giardino giardino;
    private final PlantsCounter plantsCounter;
    private final CarriolaFragment fragment;
    private final int DELAY = 600;
    private boolean holding = false;
    private final boolean isOrto;

    public CarriolaVarietaAdapter(@NonNull List<Pair<Varieta, Integer>> data, PlantsCounter plantsCounter, Giardino giardino, Boolean isOrto,
                                  CarriolaFragment fragment, CarriolaOrtaggiAdapter parentAdapter) {
        this.mData = data;
        this.mParentAdapter = parentAdapter;
        this.giardino = giardino;
        this.plantsCounter = plantsCounter;
        this.fragment = fragment;
        this.isOrto = isOrto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.carriola_varieta, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Pair<Varieta, Integer> row = mData.get(position);
        String ortaggio = row.first.getClassificazione_ortaggio();
        String varieta = row.first.getClassificazione_varieta();
        Varieta varietaObj = row.first;
        int pack = row.first.getAltro_pack();
        int color = DbPlants.getIconColor(ortaggio);
        int mBtnBkg = ColorUtils.alphaColor(color, 60);

        viewHolder.mTvName.setText(varieta);
        viewHolder.mTvDist.setText(row.first.getDistanze_piante() + " × " + row.first.getDistanze_file() + " cm");
        viewHolder.mTvCount.setText(String.format(Locale.ITALIAN, "%d", row.second));

        viewHolder.mBtnDec.setCardBackgroundColor(mBtnBkg);
        viewHolder.mBtnInc.setCardBackgroundColor(mBtnBkg);

        viewHolder.mView.setAlpha(plantsCounter.getPianteCount(ortaggio, varieta) == 0 ? 0.5f : 1f);

        viewHolder.mBtnDec.setOnClickListener(view -> {
            if (!holding) {
                viewHolder.mTvCount.setText(updateCount(ortaggio, varietaObj, -1, viewHolder));
            } else {
                holding = false;
            }
        });
        viewHolder.mBtnInc.setOnClickListener(view -> {
            if (!holding) {
                viewHolder.mTvCount.setText(updateCount(ortaggio, varietaObj, +1, viewHolder));
            } else {
                holding = false;
            }
        });

        viewHolder.mBtnRemove.setOnClickListener(view -> {
            viewHolder.mTvCount.setText(updateCount(ortaggio, varietaObj, 0, viewHolder));
        });

        viewHolder.mBtnDec.setOnTouchListener(new View.OnTouchListener() {
            Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        if (plantsCounter.getPianteCount(ortaggio, varieta) == 0) {
                            holding = false;
                            return true;
                        }
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, DELAY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        holding = false;
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            final Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    holding = true;
                    viewHolder.mTvCount.setText(updateCount(ortaggio, varietaObj, -pack, viewHolder));
                    if (plantsCounter.getPianteCount(ortaggio, varieta) > 0) {
                        mHandler.postDelayed(this, DELAY);
                    }
                }
            };
        });

        viewHolder.mBtnInc.setOnTouchListener(new View.OnTouchListener() {
            Handler mHandler;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, DELAY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        holding = false;
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            final Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    holding = true;
                    viewHolder.mTvCount.setText(updateCount(ortaggio, varietaObj, +pack, viewHolder));
                    mHandler.postDelayed(this, DELAY);
                }
            };
        });
    }

    // FIXME !!!
    @RequiresApi(api = Build.VERSION_CODES.N)
    private String updateCount(String ortaggio, Varieta varietaObj, int step, ViewHolder viewHolder) {
        String varieta = varietaObj.getClassificazione_varieta();
        int oldCount = plantsCounter.getPianteCount(ortaggio, varieta);
        int newCount = Math.max(0, step != 0 ? oldCount + step : 0);
        viewHolder.mView.setAlpha(newCount == 0 ? 0.5f : 1f);
        plantsCounter.put(ortaggio, varieta, newCount);
        fragment.updateOccupiedArea(varietaObj.calcArea() * (newCount - oldCount));
        mParentAdapter.updateCount(((View) viewHolder.mView.getParent().getParent()).findViewById(R.id.carriola_ortaggio_info), ortaggio);
        if (!isOrto) {
            giardino.setCarriola(plantsCounter);
            DbUsers.updateGiardino(giardino);
        }
        return Integer.toString(newCount);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final TextView mTvName;
        private final TextView mTvDist;
        private final TextView mTvCount;
        private final MaterialCardView mBtnDec;
        private final MaterialCardView mBtnInc;
        private final MaterialCardView mBtnRemove;

        ViewHolder(View view) {
            super(view);
            mView = view.findViewById(R.id.carriola_varieta);
            mTvName = view.findViewById(R.id.carriola_varieta_name);
            mTvDist = view.findViewById(R.id.carriola_varieta_dist);
            mTvCount = view.findViewById(R.id.carriola_varieta_count);
            mBtnDec = view.findViewById(R.id.carriola_varieta_decrement_btn);
            mBtnInc = view.findViewById(R.id.carriola_varieta_increment_btn);
            mBtnRemove = view.findViewById(R.id.carriola_variete_remove_btn);
        }
    }

}
