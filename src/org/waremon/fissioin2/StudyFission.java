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
import android.widget.ImageView;
import android.widget.LinearLayout;


public class StudyFission extends Activity implements OnClickListener {
    
	ImageButton btn[] = new ImageButton[3];
	LinearLayout linLay;
	int mPageFlag;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //タイトルバーを非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.study);

        mPageFlag = 1;
        
        linLay = (LinearLayout) findViewById(R.id.linearStudy);
        linLay.setBackgroundResource(getResources().getIdentifier("fission2_study"+mPageFlag, "drawable", getPackageName()));
        
        btn[0] = (ImageButton)findViewById(R.id.backButton);
        btn[0].setOnClickListener(this);
        btn[1] = (ImageButton)findViewById(R.id.nextButton);
        btn[1].setOnClickListener(this);
        btn[2] = (ImageButton)findViewById(R.id.topButton);
        btn[2].setOnClickListener(this);
        
        getAd();
    }
        
    public void goTop() {
    	Intent i = new Intent(this, Top.class);
    	startActivity(i);
    	finish();
    }

	@Override
	public void onClick(View v) {
		if (v == btn[2]) {
			goTop();
		}
		if(mPageFlag == 1) {
			//back
			if (v == btn[0]) {
				goTop();
			//next
			} else if (v == btn[1]) {
				mPageFlag ++;
				linLay.setBackgroundResource(getResources().getIdentifier("fission2_study"+mPageFlag, "drawable", getPackageName()));
		        getAd();
			}
		} else if(mPageFlag < 6) {
			//back
			if (v == btn[0]) {
				mPageFlag --;
			    linLay.setBackgroundResource(getResources().getIdentifier("fission2_study"+mPageFlag, "drawable", getPackageName()));
		        getAd();
			//next
			} else if (v == btn[1]) {
				mPageFlag ++;
			    linLay.setBackgroundResource(getResources().getIdentifier("fission2_study"+mPageFlag, "drawable", getPackageName()));
		        getAd();
			}
		} else if(mPageFlag == 6) {
			//back
			if (v == btn[0]) {
				mPageFlag --;
			    linLay.setBackgroundResource(getResources().getIdentifier("fission2_study"+mPageFlag, "drawable", getPackageName()));
		        getAd();
			//next
			} else if (v == btn[1]) {
				goTop();
			}
		}
	}
	  //backボタン押したとき動作しないようにする！！
  	@Override
  	public boolean dispatchKeyEvent(KeyEvent event) {
  		if (event.getAction() == KeyEvent.ACTION_DOWN) {
  			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
  				return true;
  			}
  		}
  		return super.dispatchKeyEvent(event);
  	}

  	public void getAd() {
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
}
