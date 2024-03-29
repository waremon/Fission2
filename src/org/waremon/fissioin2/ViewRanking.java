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
				RankText[i].setText(String.valueOf(i+1)+"位");
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
		dialog.setTitle("ランキング取得中");
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
		map.put("hokkaido", "北海道");
		map.put("aomori", "青森");
		map.put("iwate", "岩手");
		map.put("miyagi", "宮城");
		map.put("akita", "秋田");
		map.put("yamagata", "山形");
		map.put("fukushima", "福島");
		map.put("ibaraki", "茨城");
		map.put("tochigi", "栃木");
		map.put("gunma", "群馬");
		map.put("saitama", "埼玉");
		map.put("chiba", "千葉");
		map.put("tokyo", "東京");
		map.put("kanagawa", "神奈川");
		map.put("niigata", "新潟");
		map.put("toyama", "富山");
		map.put("ishikawa", "石川");
		map.put("fukui", "福井");
		map.put("yamanashi", "山梨");
		map.put("nagano", "長野");
		map.put("gifu", "岐阜");
		map.put("shizuoka", "静岡");
		map.put("aichi", "愛知");
		map.put("mie", "三重");
		map.put("siga", "滋賀");
		map.put("kyoto", "京都");
		map.put("osaka", "大阪");
		map.put("hyogo", "兵庫");
		map.put("nara", "奈良");
		map.put("wakayama", "和歌山");
		map.put("tottori", "鳥取");
		map.put("shimane", "島根");
		map.put("okayama", "岡山");
		map.put("hiroshima", "広島");
		map.put("yamaguchi", "山口");
		map.put("tokushima", "徳島");
		map.put("kagawa", "香川");
		map.put("ehime", "愛媛");
		map.put("kouchi", "高知");
		map.put("fukuoka", "福岡");
		map.put("saga", "佐賀");
		map.put("nagasaki", "長崎");
		map.put("kumamoto", "熊本");
		map.put("oita", "大分");
		map.put("miyazaki", "宮崎");
		map.put("kagoshima", "鹿児島");
		map.put("okinawa", "沖縄");
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
}
