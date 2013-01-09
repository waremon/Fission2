package org.waremon.fissioin2;

import javax.microedition.khronos.opengles.GL10;

public class MyTarget {
	public float mAngle;//�W�I�̊p�x
	public float mX, mY;//�W�I�̈ʒu
	public float mSize;//�W�I�̃T�C�Y
	public float mSpeed;//�W�I�̈ړ����x
	public float mTurnAngle;//�W�I�̐���p�x
	
	public MyTarget(float x, float y, float angle,
			float size, float speed, float turnAngle) {
		this.mX = x;
		this.mY = y;
		this.mAngle = angle;
		this.mSize = size;
		this.mSpeed = speed;
		this.mTurnAngle = turnAngle;
	}
	
	//�W�I�𓮂���
	public void move(int uranium){
		float theta = mAngle / 180.0f *(float)Math.PI;
		//���˕Ԃ菈��
		if ((mX >=  1.0f && Math.cos(theta) > 0) || (mX <= -1.0f && Math.cos(theta) < 0)) mAngle = 180 - mAngle;
		if ((mY >=  1.5f && Math.sin(theta) > 0) || (mY <= -1.5f && Math.sin(theta) < 0)) mAngle *= -1;
		
		theta = mAngle / 180.0f *(float)Math.PI;
		mX = mX + (float)Math.cos(theta) * mSpeed;
		mY = mY + (float)Math.sin(theta) * mSpeed;
		if (uranium == 1 && mSize < 0.25f) {
			mSize += 0.005f;
		}
	}
	
	//�|�C���g�������蔻��͈͓̔�����Ԃ�
	public boolean isPointInside(float x, float y) {
		//�W�I�ƃ^�b�`���ꂽ�|�C���g�Ƃ̋������v�Z����
		float dx = x - mX;
		float dy = y - mY;
		float distance = (float) Math.sqrt(dx*dx + dy*dy);
		
		if(distance <= mSize * 0.5f) {
			return true;
		}
		return false;
	}
	
	//�W�I��`�悷��
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
