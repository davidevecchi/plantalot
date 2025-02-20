package com.plantalot.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.color.MaterialColors;
import com.plantalot.R;
import com.plantalot.classes.Orto;
import com.plantalot.utils.IntPair;


public class AiuolaView extends LinearLayout {
	
	Orto orto;
	FrameLayout ortoView;
	Context context;
	
	public AiuolaView(Context context, Orto orto, IntPair size) {
		super(context);
		this.context = context;
		this.orto = orto;
		View view = inflate(context, R.layout.component_aiuola, this);
		ortoView = view.findViewById(R.id.component_aiuola);
		setSize(size);
	}
	
	public void setSize(IntPair size) {
		int weight = (int) (6000f / (double) orto.calcOrtoDim().max());
		LayoutParams params = (LayoutParams) ortoView.getLayoutParams();
		params.width = size.x;
		params.height = size.y;
		ortoView.setLayoutParams(params);
		FrameLayout ortoBkg = ortoView.findViewById(R.id.component_aiuola_background);
		GradientDrawable bkg = (GradientDrawable) ortoBkg.getBackground();
		bkg.setColor(MaterialColors.harmonizeWithPrimary(context, Color.parseColor("#eeddcc")));  // FIXME colors
		bkg.setStroke(weight, MaterialColors.harmonizeWithPrimary(context, Color.parseColor("#603325")));  // FIXME colors
		bkg.setCornerRadius(weight);
		ortoView.setPadding(weight, weight, weight, weight);
	}
}
