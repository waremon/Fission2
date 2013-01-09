package org.waremon.fissioin2;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ViewRanking extends Activity implements WebAPIListener {

	/**
	 * WebAPI
	 */
	WebAPI mWebAPI;

	/**
	 * TAG
	 */
	private final static String TAG = "GAE_CLIENT";

	private TextView pointView;

	private Spinner spinner;
	private Button btnSubmit;
	private Button btnNoSubmit;

	private TextView[] AreaText;
	private TextView[] ScoreText;
	private TextView[] RankText;
	private String[] AreaString = new String[47];
	private String[] ScoreString = new String[47];

	private ProgressDialog dialog;
	
	HashMap<String, String> map = new HashMap<String, String>();

	final Handler handler=new Handler() {
		public void handleMessage(Message msg) {
			for (int i=0; i<47; i++) {
				RankText[i].setText(String.valueOf(i+1)+"à ");
				AreaText[i].setText(AreaString[i]);
				ScoreText[i].setText(ScoreString[i]);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gaeclient);

		setMap();
		
		pointView = (TextView) findViewById(R.id.globalScore);
		pointView.setText(String.valueOf(Global.globalScore));

		spinner = (Spinner) findViewById(R.id.spinner1);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Global.globalArea = reHash(String.valueOf(spinner.getSelectedItem()));
				updateScore();
			}
		});
		btnNoSubmit = (Button) findViewById(R.id.btnNoSubmit);
		btnNoSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toTop();
			}
		});

		AreaText = new TextView[47];
		ScoreText = new TextView[47];
		RankText = new TextView[47];
		for (int i=0; i<47; i++) {
			RankText[i] = (TextView) findViewById(getResources().getIdentifier("textView"+i+"0", "id", getPackageName()));
			AreaText[i] = (TextView) findViewById(getResources().getIdentifier("textView"+i+"1", "id", getPackageName()));
			AreaString[i] = "";
			ScoreText[i] = (TextView) findViewById(getResources().getIdentifier("textView"+i+"2", "id", getPackageName()));
			ScoreString[i] = "";
		}

		mWebAPI = new WebAPI(this);
		String key[] = {"score", "area", "flag"};
		String value[] = {"0", "hokkaido", "0"};
		mWebAPI.setEventListener(this);
		mWebAPI.sendData(key, value);
		dialog = new ProgressDialog(this);
		dialog.setTitle("ÉâÉìÉLÉìÉOéÊìæíÜ");
		dialog.setMessage("Now Loading...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
		
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

	@Override
	public void onLoad(int type, String json) {
		if(type == mWebAPI.ACT_UPLOAD){

			try {
				JSONArray jsonArray = new JSONArray(json);

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i); 
					String mArea = jsonObj.getString("area");
					String mScore = jsonObj.getString("score");
					AreaString[i] = map.get(mArea);
					ScoreString[i] = mScore;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			handler.sendEmptyMessage(0);
		}
		dialog.dismiss();
	}

	public void updateScore() {
		Global.UpdateFlag = 1;
		Global.ViewOnce = 2;
		Intent i = new Intent(this, UpdateRanking.class);
		startActivity(i);
		finish();
	}

	public void toTop() {
		Intent i = new Intent(this, Top.class);
		startActivity(i);
		finish();
	}

	public void setMap() {
		map.put("hokkaido", "ñkäCìπ");
		map.put("aomori", "ê¬êX");
		map.put("iwate", "ä‚éË");
		map.put("miyagi", "ã{èÈ");
		map.put("akita", "èHìc");
		map.put("yamagata", "éRå`");
		map.put("fukushima", "ïüìá");
		map.put("ibaraki", "àÔèÈ");
		map.put("tochigi", "ì»ñÿ");
		map.put("gunma", "åQîn");
		map.put("saitama", "çÈã ");
		map.put("chiba", "êÁót");
		map.put("tokyo", "ìåãû");
		map.put("kanagawa", "ê_ìﬁêÏ");
		map.put("niigata", "êVäÉ");
		map.put("toyama", "ïxéR");
		map.put("ishikawa", "êŒêÏ");
		map.put("fukui", "ïüà‰");
		map.put("yamanashi", "éRóú");
		map.put("nagano", "í∑ñÏ");
		map.put("gifu", "äÚïå");
		map.put("shizuoka", "ê√â™");
		map.put("aichi", "à§ím");
		map.put("mie", "éOèd");
		map.put("siga", "é†âÍ");
		map.put("kyoto", "ãûìs");
		map.put("osaka", "ëÂç„");
		map.put("hyogo", "ï∫å…");
		map.put("nara", "ìﬁó«");
		map.put("wakayama", "òaâÃéR");
		map.put("tottori", "íπéÊ");
		map.put("shimane", "ìáç™");
		map.put("okayama", "â™éR");
		map.put("hiroshima", "çLìá");
		map.put("yamaguchi", "éRå˚");
		map.put("tokushima", "ìøìá");
		map.put("kagawa", "çÅêÏ");
		map.put("ehime", "à§ïQ");
		map.put("kouchi", "çÇím");
		map.put("fukuoka", "ïüâ™");
		map.put("saga", "ç≤âÍ");
		map.put("nagasaki", "í∑çË");
		map.put("kumamoto", "åFñ{");
		map.put("oita", "ëÂï™");
		map.put("miyazaki", "ã{çË");
		map.put("kagoshima", "é≠éôìá");
		map.put("okinawa", "â´ìÍ");
	}

	public String reHash(String value) {
		String key = null;
		for (Map.Entry<String, String> keyValue : map.entrySet()) {
			if (keyValue.getValue().equals(value)) {
				key = keyValue.getKey();
			}
		}
		return key;
	}
	  //backÉ{É^ÉìâüÇµÇΩÇ∆Ç´ìÆçÏÇµÇ»Ç¢ÇÊÇ§Ç…Ç∑ÇÈÅIÅI
  	@Override
  	public boolean dispatchKeyEvent(KeyEvent event) {
  		if (event.getAction() == KeyEvent.ACTION_DOWN) {
  			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
  				return true;
  			}
  		}
  		return super.dispatchKeyEvent(event);
  	}
}
