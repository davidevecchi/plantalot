package com.plantalot;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link View.OnClickListener} used to translate the product grid sheet downward on
 * the Y-axis when the navigation icon in the toolbar is pressed.
 */
public class NavigationIconClickListener implements View.OnClickListener {
	
	private final AnimatorSet animatorSet = new AnimatorSet();
	private final Context context;
	private final View sheet;
	private final Interpolator interpolator;
	private final int height;
	private boolean backdropShown = false;
	private final Drawable openIcon;
	private final Drawable closeIcon;
	private final int translateY;
	
	NavigationIconClickListener(Context context, View sheet) {
		this(context, sheet, null);
	}
	
	NavigationIconClickListener(Context context, View sheet, @Nullable Interpolator interpolator) {
		this(context, sheet, interpolator, null, null, 0);
	}
	
	NavigationIconClickListener(
			Context context, View sheet, @Nullable Interpolator interpolator,
			@Nullable Drawable openIcon, @Nullable Drawable closeIcon, int translateY) {
		this.context = context;
		this.sheet = sheet;
		this.interpolator = interpolator;
		this.openIcon = openIcon;
		this.closeIcon = closeIcon;
		this.translateY = translateY;
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		height = displayMetrics.heightPixels;
	}
	
	@Override
	public void onClick(View view) {
		backdropShown = !backdropShown;
		
		// Cancel the existing animations
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
		RecyclerView fl = sheet.findViewById(R.id.recycler_home_orti);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) fl.getLayoutParams();
		params.bottomMargin = backdropShown ? translateY : 0;
		fl.setLayoutParams(params);
		
	}
	
	private void updateIcon(View view) {
		if (openIcon != null && closeIcon != null) {
			if (!(view instanceof ImageView)) {
				throw new IllegalArgumentException("updateIcon() must be called on an ImageView");
			}
			if (backdropShown) {
				((ImageView) view).setImageDrawable(closeIcon);
			} else {
				((ImageView) view).setImageDrawable(openIcon);
			}
		}
	}
}
