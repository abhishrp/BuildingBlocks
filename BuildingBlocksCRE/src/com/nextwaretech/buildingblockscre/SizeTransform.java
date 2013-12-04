package com.nextwaretech.buildingblockscre;

import android.content.Context;

public class SizeTransform {

	public static int dpToPixel(Context context, int dp) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
