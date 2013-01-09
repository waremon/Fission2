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
	private ImageButton mRetryButton;//���g���C�{�^��
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //�t���X�N���[���\��
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //�^�C�g���o�[���\��
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Global.mainActivity = this;
        
        final MyRenderer renderer = new MyRenderer(this);//MyRenderer�̐���
        //GLSurfaceView�̐���
        MyGLSurfaceView glSurfaceView = new MyGLSurfaceView(this);
        //GLSurfaceView��MyRenderer��K�p
        glSurfaceView.setRenderer(renderer);
        
        setContentView(glSurfaceView);//�r���[��GLSuerfaceView�Ɏw��
        
        //�{�^���̃��C�A�E�g
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity =
        		Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        params.setMargins(0, 150, 0, 0);
        //�{�^���̍쐬
        this.mRetryButton = new ImageButton(this);
        this.mRetryButton.setImageResource(getResources().getIdentifier("rankingbutton", "drawable", getPackageName()));
        this.mRetryButton.setBackgroundDrawable(null);
        hideRetryButton();
        addContentView(mRetryButton, params);
        //�C�x���g�̒ǉ�
        this.mRetryButton.setOnClickListener(
        		new ImageButton.OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				hideRetryButton();
        				//renderer.startNewGame();
        				startRanking();
        			}
        });
        
        //�f�o�b�O���[�h�ł��邩�𔻒肷��
        try {
        	PackageManager pm = getPackageManager();
        	ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
        	Global.isDebuggable = (ApplicationInfo.FLAG_DEBUGGABLE ==
        			(ai.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (NameNotFoundException e) {
        	e.printStackTrace();
        }
    }
    
    //���g���C�{�^����\������
    public void showRetryButton(){
    	mRetryButton.setVisibility(View.VISIBLE);
    }
    
    //���g���C�{�^�����\���ɂ���
    public void hideRetryButton() {
    	mRetryButton.setVisibility(View.GONE);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	//�e�N�X�`�����폜����
    	TextureManager.deleteAll(Global.gl);
    }
    
    public void startRanking() {
    	Intent i = new Intent(this, ViewRanking.class);
    	startActivity(i);
    	finish();
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