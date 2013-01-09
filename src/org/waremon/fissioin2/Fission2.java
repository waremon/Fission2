package org.waremon.fissioin2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class Fission2 extends Activity {
    /** Called when the activity is first created. */
	private ImageButton mRetryButton;//リトライボタン
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //フルスクリーン表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //タイトルバーを非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Global.mainActivity = this;
        
        final MyRenderer renderer = new MyRenderer(this);//MyRendererの生成
        //GLSurfaceViewの生成
        MyGLSurfaceView glSurfaceView = new MyGLSurfaceView(this);
        //GLSurfaceViewにMyRendererを適用
        glSurfaceView.setRenderer(renderer);
        
        setContentView(glSurfaceView);//ビューをGLSuerfaceViewに指定
        
        //ボタンのレイアウト
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity =
        		Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        params.setMargins(0, 150, 0, 0);
        //ボタンの作成
        this.mRetryButton = new ImageButton(this);
        this.mRetryButton.setImageResource(getResources().getIdentifier("rankingbutton", "drawable", getPackageName()));
        this.mRetryButton.setBackgroundDrawable(null);
        hideRetryButton();
        addContentView(mRetryButton, params);
        //イベントの追加
        this.mRetryButton.setOnClickListener(
        		new ImageButton.OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				hideRetryButton();
        				//renderer.startNewGame();
        				startRanking();
        			}
        });
        
        //デバッグモードであるかを判定する
        try {
        	PackageManager pm = getPackageManager();
        	ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
        	Global.isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE ==
        			(ai.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (NameNotFoundException e) {
        	e.printStackTrace();
        }
    }
    
    //リトライボタンを表示する
    public void showRetryButton(){
    	mRetryButton.setVisibility(View.VISIBLE);
    }
    
    //リトライボタンを非表示にする
    public void hideRetryButton() {
    	mRetryButton.setVisibility(View.GONE);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	//テクスチャを削除する
    	TextureManager.deleteAll(Global.gl);
    }
    
    public void startRanking() {
    	Intent i = new Intent(this, ViewRanking.class);
    	startActivity(i);
    	finish();
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