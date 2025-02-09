package com.plantalot.animations;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.plantalot.R;

/**
 * {@link View.OnClickListener} used to translate the product grid sheet downward on
 * the Y-axis when the navigation icon in the toolbar is pressed.
 */
public class NavigationIconClickListener implements View.OnClickListener {
	
	private final AnimatorSet animatorSet = new AnimatorSet();
	private final View sheet;
	private final Interpolator interpolator;
	private boolean backdropShown = false;
	private final Integer openIcon;
	private final Integer closeIcon;
	private final LinearLayout drawer;
	
	public NavigationIconClickListener(
			Context context, View sheet, @Nullable Interpolator interpolator,
			Integer openIcon, Integer closeIcon, LinearLayout drawer) {
		this.sheet = sheet;
		this.interpolator = interpolator;
		this.openIcon = openIcon;
		this.closeIcon = closeIcon;
		this.drawer = drawer;
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	}
	
	@Override
	public void onClick(View view) {
		backdropShown = !backdropShown;
		drawer.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int translateY = drawer.getMeasuredHeight();
		
		// Cancel the existing com.plantalot.animations
		animatorSet.removeAllListeners();
		animatorSet.end();
		animatorSet.cancel();
		
		updateIcon(view);
		
		// Translation animation
		ObjectAnimator animator = ObjectAnimator.ofFloat(sheet, "translationY", backdropShown ? translateY : 0);
		animator.setDuration(300);
		if (interpolator != null) {
			animator.setInterpolator(interpolator);
		}
		animatorSet.play(animator);
		animator.start();
		
		// Add bottom margin to RecyclerView to not crop the content
//		RecyclerView fl = sheet.findViewById(R.id.all_plants_fl_card_list_recycler);  // FIXME !!!!!!!!!!!
		LinearLayout fl = sheet.findViewById(R.id.home_fl_content);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fl.getLayoutParams();
		params.bottomMargin = backdropShown ? translateY : 0;
		fl.setLayoutParams(params);

	}
	
	private void updateIcon(View view) {
		if (openIcon != null && closeIcon != null) {
			if (!(view instanceof ImageView)) {
				throw new IllegalArgumentException("updateIcon() must be called on an ImageView");
			}
			((ImageView) view).setImageResource(backdropShown ? closeIcon : openIcon);
			
		}
	}
}
