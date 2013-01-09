package org.waremon.fissioin2;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;


public class Top extends Activity implements OnClickListener {

	ImageButton btn[] = new ImageButton[3];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//タイトルバーを非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.top);

		btn[0] = (ImageButton)findViewById(R.id.topButton1);
		btn[0].setOnClickListener(this);
		btn[1] = (ImageButton)findViewById(R.id.topButton2);
		btn[1].setOnClickListener(this);
		btn[2] = (ImageButton)findViewById(R.id.topButton3);
		btn[2].setOnClickListener(this);

		// create adView
		AdView adView = new AdView(this, AdSize.BANNER, "a1508cfa60cff60");
		// get View for ad
		LinearLayout layout = (LinearLayout)findViewById(R.id.adlayout);
		// add adView
		layout.addView(adView);
		// request ad
		AdRequest request = new AdRequest();
		adView.loadAd(request);
	}

	public void startGame() {
		Intent i = new Intent(this, Fission2.class);
		startActivity(i);
		finish();
	}

	public void startStudy() {
		Intent i = new Intent(this, StudyFission.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	public void startHowto() {
		Intent i = new Intent(this, HowTo.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onClick(View v) {
		if(v == btn[2]) {
			startGame();
		} else if (v == btn[0]) {
			startStudy();
		} else if (v == btn[1]) {
			startHowto();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		} else {
			this.finish();
			this.moveTaskToBack(true);
			return false;
		}
	}
}