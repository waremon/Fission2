package org.waremon.fissioin2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;

public class MyRenderer extends Activity implements GLSurfaceView.Renderer {
	
	public static final int URANIUM_NUM = 20;
	public static final int NEUTRON_NUM = 89;
	public int mNeutronNum;
	private static final int GAME_INTERVAL = 60;
	private static int mCollFlug[] = new int[10000];
	
	//コンテキスト
	private Context mContext;
	
	private int mWidth;
	private int mHeight;
	
	//テクスチャ
	private int mBgTexture;//背景テクスチャ
	private int mUraniumTexture;//ウラン用テクスチャ
	private int mBanTexture;//ウラン爆発用テクスチャ
	private int mNeutronTexture;//中性子用テクスチャ
	private int mNumberTexture;//数字用テクスチャ
	private int mGameOverTexture;//ゲームオーバー用テクスチャ
	private int mSeigyoTexture;//制御棒用テクスチャ
	private int mHandTexture;
	private int mSwitchoffTexture;//
	private int mSwitchonTexture;//
	private int[] mMeterTexture = new int[10];//
	
	private float mSeigyoWidth = 0.1f;//0.02f;
	private float mSeigyoHeight = 3.0f;
	
	private MyTarget[] mUranium = new MyTarget[URANIUM_NUM];
	private MyTarget[] mNeutron = new MyTarget[NEUTRON_NUM];
	private MyTarget mBan;
	
	private int mScore;//得点
	private long mStartTime;//開始時間
	private float mSeigyoState;
	private int mTemp;
	private int mTargetTemp;
	private int mSwitchTime;
	private int mSwitchTimeRest;
	
	private boolean mGameOverFlag;
	private boolean mBanFlag;
	private boolean mSwitchState;
	
	private Handler mHandler = new Handler();
	
	public MyRenderer(Context context) {
		this.mContext = context;
		startNewGame();
	}
		
	public void startNewGame() {
		Random rand = Global.rand;
		//標的の状態を初期化する
		for (int i = 0; i < URANIUM_NUM; i++) {
			//標的の初期座標は(-1.0 ~ 1.0, -1.0 ~ 1.0)の間のランダムな地点にする
			float x;
			float y;
			do {
				x = rand.nextFloat() * 2.0f - 1.0f;
				y = rand.nextFloat() * 2.0f - 1.0f;
			} while(!isUniqPos(i, x, y));
			//角度をランダムに設定する
			float angle = rand.nextInt(360);
			//標的の大きさ
			float size = 0.25f;//rand.nextFloat() * 0.25f + 0.25f;
			//標的の移動速度を0.01~0.02の間でランダムに決定する
			float speed = 0.005f;//rand.nextFloat() * 0.01f + 0.01f;
			//標的の旋回角度を-0.2f~2.0fの間でランダムに決定する
			float turnAngle = 0f;//rand.nextFloat() * 4.0f - 2.0f;
			mUranium[i] = new MyTarget (x, y, angle, size, speed, turnAngle);
		}
		for (int i = 0; i < NEUTRON_NUM; i++) {
			mNeutron[i] = new MyTarget (0, 0, -45 , 0.05f, 0.04f, 0);
		}
		mBan = new MyTarget(0,0,0,0.25f,0,0);
		this.mScore = 0;
		this.mStartTime = System.currentTimeMillis();
		this.mGameOverFlag = false;
		this.mBanFlag = false;
		this.mSwitchState = false;
		this.mSeigyoState = 0.1f;
		this.mTemp = 1;
		this.mNeutronNum = 1;
		this.mTargetTemp = 4;
		this.mSwitchTime = 10;
		this.mSwitchTimeRest = GAME_INTERVAL;
	}
	
	public boolean isUniqPos(int i, float x, float y) {
		for (int j=0; j<i; j++) {
			if (Math.sqrt(Math.pow(x - mUranium[j].mX, 2) + Math.pow(y - mUranium[j].mY, 2)) < 0.25f) {
				return false;
			}
		}
		return true;
	}
	
	private long mFpsCountStartTime = System.currentTimeMillis();
	private int mFramesInSecond = 0;
	private int mFps = 0;
	public void renderMain(GL10 gl) {
		//経過時間を計算する
		int passedTime =
				(int)(System.currentTimeMillis() - mStartTime) / 1000;
		int remainTime = GAME_INTERVAL - passedTime;
		if (remainTime <= 0) {
			remainTime = 0;
			if(!mGameOverFlag) {
				Global.globalScore = mScore;
				mGameOverFlag = true;
				//Global.mainActivity.showRetryButton()をUIスレッド上で実行する
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Global.mainActivity.showRetryButton();
					}
				});
			}
		}
		
		//Global.randとmTargetをローカルにキャッシュする
		Random rand = Global.rand;
		if (mSwitchTimeRest - remainTime == mSwitchTime) {
			mSwitchTimeRest -= mSwitchTime;
			mSwitchTime = 10 - rand.nextInt(5);
			mTargetTemp = rand.nextInt(7)+2;
		}
		MyTarget[] uranium = mUranium;
		MyTarget[] neutron = mNeutron;
		MyTarget ban = mBan;
		//すべての標的を１つずつ動かす
		for(int i=0; i < URANIUM_NUM; i++) {
			if (uranium[i].mSize > 0.24f) {
				//ウラン同士の衝突検知
				for (int j=0; j<i; j++) {
					if(isCollision(i, j, uranium) && uranium[j].mSize > 0.24f){
						break;
					}
				}
				//ウランと中性子の衝突->ウラン消える
				for (int j=0; j<mNeutronNum; j++) {
					if(isCollisionWithNeutron(i, j, uranium, neutron, ban)){
						break;
					}
				}
			}
			//標的を動かす
			uranium[i].move(1);
		}

		for(int i=0; i<mNeutronNum; i++) {
			if(isCollisionWithSeigyo(i, neutron)) {
				break;
			}
		}
		
		for(int i=0; i< mNeutronNum; i++) {
			neutron[i].move(0);
		}
		//背景を描画する
		GraphicUtil.drawTexture(gl, 0.0f, 0.0f, 2.0f, 3.0f,
				mBgTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		//ウランを描画する
		for (int i =0; i < URANIUM_NUM; i++) {
			uranium[i].draw(gl, mUraniumTexture);
		}
		//中性子を描画する
		for (int i=0; i<mNeutronNum; i++) {
			neutron[i].draw(gl, mNeutronTexture);
			//Log.d("Debug", String.format("x:%f y:%f",neutron[i].mX, neutron[i].mY));
		}
		//爆発を描画する
		if (mBanFlag == true) {
			ban.draw(gl, mBanTexture);
			mBanFlag = false;
		}
		//制御棒を描画する
		if (mSwitchState == false && mSeigyoState > 0.05f) {
			mSeigyoState -= 0.01f; 
		} else if (mSwitchState == true && mSeigyoState < 2.9f) {
			mSeigyoState += 0.01f;
		}
		GraphicUtil.drawTexture(gl, -0.5f, mSeigyoState, mSeigyoWidth, mSeigyoHeight, mSeigyoTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		GraphicUtil.drawTexture(gl, 0, mSeigyoState, mSeigyoWidth, mSeigyoHeight, mSeigyoTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		GraphicUtil.drawTexture(gl, 0.5f, mSeigyoState, mSeigyoWidth, mSeigyoHeight, mSeigyoTexture, 1.0f, 1.0f, 1.0f, 1.0f);

		//得点(反応回数)を描画する
		GraphicUtil.drawNumbers(gl, -0.6f, 1.4f, 0.1f, 0.2f, mNumberTexture, mScore, 8, 1.0f, 1.0f, 1.0f, 1.0f, true);
		//体力を描画する
		GraphicUtil.drawNumbers(gl, -0.8f, -1.4f, 0.1f, 0.2f, mNumberTexture, remainTime, 2, 1.0f, 1.0f, 1.0f, 1.0f, false);
		//meter
		mTemp = mNeutronNum/10 + 1;
		GraphicUtil.drawTexture(gl, 1.0f-0.15f, 1.5f-0.5f, 0.3f, 1.0f, mMeterTexture[mTemp], 1.0f, 1.0f, 1.0f, 1.0f);
		//hand
		GraphicUtil.drawTexture(gl, 1.0f-0.15f-0.15f, 1.5f-0.5f-0.32f+0.085f*(mTargetTemp-1), 0.3f, 0.15f, mHandTexture, 1.0f, 1.0f, 1.0f, 1.0f);		
		if(mGameOverFlag) {
			//GraphicUtil.drawTexture(gl, 0.0f, 0.0f, 2.0f, 1.5f, mGameOverTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		}
		//スイッチ
		if (mSwitchState) {
			GraphicUtil.drawTexture(gl, 0.75f, -1.25f, 0.5f, 0.5f, mSwitchonTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		} else {
			GraphicUtil.drawTexture(gl, 0.75f, -1.25f, 0.5f, 0.5f, mSwitchoffTexture, 1.0f, 1.0f, 1.0f, 1.0f);
		}
		gl.glDisable(GL10.GL_BLEND);
		//Log.d("Debug", String.format("neutron:%d",mNeutronNum));
	}

	public boolean isCollision(int i, int j, MyTarget[] uranium) {
		if(Math.sqrt(Math.pow(uranium[i].mX - uranium[j].mX, 2) + Math.pow(uranium[i].mY - uranium[j].mY, 2)) < 0.25f) {
			if (!wasCollisioned(i, j)) {
				float iAngle = uranium[i].mAngle;
				float jAngle = uranium[j].mAngle;
				uranium[i].mAngle = jAngle;
				uranium[j].mAngle = iAngle;
				mCollFlug[10*i+j] = 1;
				return true;
			}
			return false;
		} else {
			mCollFlug[100*i+j] = 0;
			return false;
		}
	}
	
	public boolean wasCollisioned(int i, int j) {
		if (mCollFlug[10*i+j] == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isCollisionWithNeutron(int i, int j, MyTarget[] uranium, MyTarget[] neutron, MyTarget ban) {
		if(Math.sqrt(Math.pow(uranium[i].mX - neutron[j].mX, 2) + Math.pow(uranium[i].mY - neutron[j].mY, 2)) < 0.3f) {
			//爆発
			ban.mX = uranium[i].mX;
			ban.mY = uranium[i].mY;
			ban.mAngle = uranium[i].mAngle;
			//ウラン消す
			Random rand = Global.rand;
			float x;
			float y;
			do {
				x = rand.nextFloat() * 2.0f - 1.0f;
				y = rand.nextFloat() * 2.0f - 1.0f;
			} while(!isUniqPos(i, x, y));
			uranium[i].mX = x;
			uranium[i].mY = y;
			uranium[i].mSize = 0.001f;
			//中性子増やす
			if (mNeutronNum < NEUTRON_NUM) {
				neutron[mNeutronNum].mX = neutron[j].mX;
				neutron[mNeutronNum].mY = neutron[j].mY;
				neutron[mNeutronNum].mAngle = neutron[j].mAngle - 45;
				neutron[j].mAngle = neutron[j].mAngle + 45;
				mNeutronNum ++;
			}
			//得点
			if(mTemp == mTargetTemp && !mGameOverFlag) mScore ++;
			mBanFlag = true;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isCollisionWithSeigyo(int i, MyTarget[] neutron) {
		if (neutron[i].mY > mSeigyoState - mSeigyoHeight/2) {
			if(Math.abs(neutron[i].mX) < mSeigyoWidth/2 || ((Math.abs(neutron[i].mX) < 0.5f + mSeigyoWidth/2 && Math.abs(neutron[i].mX) > 0.5f - mSeigyoWidth/2))) {
				if(mNeutronNum > 1) {
					neutron[i].mX = neutron[mNeutronNum-1].mX;
					neutron[i].mY = neutron[mNeutronNum-1].mY;
					neutron[i].mAngle = neutron[mNeutronNum-1].mAngle;
					mNeutronNum --;
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		
		//OPenGLの座標系などを設定している
		gl.glViewport(0, 0, mWidth, mHeight);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		gl.glLoadIdentity();
		gl.glOrthof(-1.0f, 1.0f, -1.5f, 1.5f, 0.5f, -0.5f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		//画面をクリアしている
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		renderMain(gl);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		this.mWidth = width;
		this.mHeight = height;
		
		Global.gl = gl;
		
		//テクスチャをロードする
		loadTextures(gl);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
	}
	
	//テクスチャを読み込むメソッド
	private void loadTextures(GL10 gl) {
		Resources res = mContext.getResources();
		this.mBgTexture = GraphicUtil.loadTexture(gl, res, R.drawable.background3);
		this.mUraniumTexture = GraphicUtil.loadTexture(gl, res, R.drawable.uran3);
		this.mNeutronTexture = GraphicUtil.loadTexture(gl, res, R.drawable.neutron3);
		this.mNumberTexture = GraphicUtil.loadTexture(gl, res, R.drawable.number_texture);
		this.mSeigyoTexture = GraphicUtil.loadTexture(gl, res, R.drawable.bar3);
		this.mGameOverTexture = GraphicUtil.loadTexture(gl, res, R.drawable.gameover);
		this.mBanTexture = GraphicUtil.loadTexture(gl, res, R.drawable.explosion);
		this.mSwitchoffTexture = GraphicUtil.loadTexture(gl, res, R.drawable.switchoff);
		this.mSwitchonTexture = GraphicUtil.loadTexture(gl, res, R.drawable.switchon);
		this.mMeterTexture[1] = GraphicUtil.loadTexture(gl, res, R.drawable.meter1);
		this.mMeterTexture[2] = GraphicUtil.loadTexture(gl, res, R.drawable.meter2);
		this.mMeterTexture[3] = GraphicUtil.loadTexture(gl, res, R.drawable.meter3);
		this.mMeterTexture[4] = GraphicUtil.loadTexture(gl, res, R.drawable.meter4);
		this.mMeterTexture[5] = GraphicUtil.loadTexture(gl, res, R.drawable.meter5);
		this.mMeterTexture[6] = GraphicUtil.loadTexture(gl, res, R.drawable.meter6);
		this.mMeterTexture[7] = GraphicUtil.loadTexture(gl, res, R.drawable.meter7);
		this.mMeterTexture[8] = GraphicUtil.loadTexture(gl, res, R.drawable.meter8);
		this.mMeterTexture[9] = GraphicUtil.loadTexture(gl, res, R.drawable.meter9);
		this.mHandTexture = GraphicUtil.loadTexture(gl, res, R.drawable.hand);
	}
	
	//画面がタッチされたときに呼ばれるメソッド
	public void touched(float x, float y) {
		if (0.25 < x && -1.0 > y) {
			if (mSwitchState) {
				mSwitchState = false;
			} else {
				mSwitchState = true;
			}
		}
	}
}