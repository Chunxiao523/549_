package com.calebdevelops.rajawalivrcardboard;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RajawaliVRExampleActivity extends RajawaliVRActivity {
	private RajawaliVRExampleRenderer mRenderer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		mRenderer = new RajawaliVRExampleRenderer(this);
		mRenderer.setSurfaceView(mSurfaceView);
		setRenderer(mRenderer);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView label = new TextView(this);
		label.setText("\t\t\tYour Score:");
		label.setTextSize(40);
		ll.addView(label);

		label = new TextView(this);
		label.setText("\t\t\t" + mRenderer.getScore());
		label.setTextSize(35);
		ll.addView(label);

		mLayout.addView(ll);
	}
}
