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
				RankText[i].setText(String.valueOf(i+1)+"��");
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
		dialog.setTitle("�����L���O�擾��");
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
		map.put("hokkaido", "�k�C��");
		map.put("aomori", "�X");
		map.put("iwate", "���");
		map.put("miyagi", "�{��");
		map.put("akita", "�H�c");
		map.put("yamagata", "�R�`");
		map.put("fukushima", "����");
		map.put("ibaraki", "���");
		map.put("tochigi", "�Ȗ�");
		map.put("gunma", "�Q�n");
		map.put("saitama", "���");
		map.put("chiba", "��t");
		map.put("tokyo", "����");
		map.put("kanagawa", "�_�ސ�");
		map.put("niigata", "�V��");
		map.put("toyama", "�x�R");
		map.put("ishikawa", "�ΐ�");
		map.put("fukui", "����");
		map.put("yamanashi", "�R��");
		map.put("nagano", "����");
		map.put("gifu", "��");
		map.put("shizuoka", "�É�");
		map.put("aichi", "���m");
		map.put("mie", "�O�d");
		map.put("siga", "����");
		map.put("kyoto", "���s");
		map.put("osaka", "���");
		map.put("hyogo", "����");
		map.put("nara", "�ޗ�");
		map.put("wakayama", "�a�̎R");
		map.put("tottori", "����");
		map.put("shimane", "����");
		map.put("okayama", "���R");
		map.put("hiroshima", "�L��");
		map.put("yamaguchi", "�R��");
		map.put("tokushima", "����");
		map.put("kagawa", "����");
		map.put("ehime", "���Q");
		map.put("kouchi", "���m");
		map.put("fukuoka", "����");
		map.put("saga", "����");
		map.put("nagasaki", "����");
		map.put("kumamoto", "�F�{");
		map.put("oita", "�啪");
		map.put("miyazaki", "�{��");
		map.put("kagoshima", "������");
		map.put("okinawa", "����");
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
	  //back�{�^���������Ƃ����삵�Ȃ��悤�ɂ���I�I
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
