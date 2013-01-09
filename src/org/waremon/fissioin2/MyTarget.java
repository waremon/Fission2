package org.waremon.fissioin2;

import javax.microedition.khronos.opengles.GL10;

public class MyTarget {
	public float mAngle;//標的の角度
	public float mX, mY;//標的の位置
	public float mSize;//標的のサイズ
	public float mSpeed;//標的の移動速度
	public float mTurnAngle;//標的の旋回角度
	
	public MyTarget(float x, float y, float angle,
			float size, float speed, float turnAngle) {
		this.mX = x;
		this.mY = y;
		this.mAngle = angle;
		this.mSize = size;
		this.mSpeed = speed;
		this.mTurnAngle = turnAngle;
	}
	
	//標的を動かす
	public void move(int uranium){
		float theta = mAngle / 180.0f *(float)Math.PI;
		//跳ね返り処理
		if ((mX >=  1.0f && Math.cos(theta) > 0) || (mX <= -1.0f && Math.cos(theta) < 0)) mAngle = 180 - mAngle;
		if ((mY >=  1.5f && Math.sin(theta) > 0) || (mY <= -1.5f && Math.sin(theta) < 0)) mAngle *= -1;
		
		theta = mAngle / 180.0f *(float)Math.PI;
		mX = mX + (float)Math.cos(theta) * mSpeed;
		mY = mY + (float)Math.sin(theta) * mSpeed;
		if (uranium == 1 && mSize < 0.25f) {
			mSize += 0.005f;
		}
	}
	
	//ポイントが当たり判定の範囲内かを返す
	public boolean isPointInside(float x, float y) {
		//標的とタッチされたポイントとの距離を計算する
		float dx = x - mX;
		float dy = y - mY;
		float distance = (float) Math.sqrt(dx*dx + dy*dy);
		
		if(distance <= mSize * 0.5f) {
			return true;
		}
		return false;
	}
	
	//標的を描画する
	public void draw(GL10 gl, int texture) {
		gl.glPushMatrix();
		{
			gl.glTranslatef(mX, mY, 0.0f);
			gl.glRotatef(mAngle, 0.0f, 0.0f, 1.0f);
			gl.glScalef(mSize, mSize, 1.0f);
			GraphicUtil.drawTexture(gl, 0.0f, 0.0f, 1.0f, 1.0f,
					texture, 1.0f, 1.0f, 1.0f, 1.0f);
		}
		gl.glPopMatrix();
	}
}
