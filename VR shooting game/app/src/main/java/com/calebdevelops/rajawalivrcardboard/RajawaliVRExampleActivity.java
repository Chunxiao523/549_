package com.calebdevelops.rajawalivrcardboard;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
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
		final LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView label = new TextView(this);
		label.setText("\t\t\tYour Score: \t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tYour Score:");
		label.setTextSize(40);
		ll.addView(label);

		final TextView score = new TextView(this);
		score.setText("\t\t\t" + mRenderer.getScore());
		score.setTextSize(35);
		ll.addView(score);


		final TextView focus = new TextView(this);
		focus.setText("\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "+" + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+ "+");
		focus.setTextSize(110);
		ll.addView(focus);
		mLayout.addView(ll);



		final TextView win = new TextView(this);
		win.setText("\n\n\t\t\t\t\t Victory!\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tVictory!" );
		win.setTextSize(50);
		win.setTextColor(Color.rgb(255, 128, 0));
		final Integer count=0;
		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(1000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// update TextView here!
								score.setText("\t\t\t" + mRenderer.getScore() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + mRenderer.getScore());
								if(mRenderer.getScore()==50){
									ll.removeAllViews();
									ll.addView(win);
									//score.setText("\t\t\t" + mRenderer.getScore()+"\n\t\t\t Victory");
								}
							}
						});
					}
				} catch (InterruptedException e) {
				}
			}
		};

		t.start();
	}
}
