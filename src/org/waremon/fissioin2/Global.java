package org.waremon.fissioin2;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Global {
	
	//MainActivity
	public static Fission2 mainActivity;
	//GL�R���e�L�X�g��ێ�����ϐ�
	public static GL10 gl;
	
	//�����_���Ȓl�𐶐�����
	public static Random rand =
			new Random(System.currentTimeMillis());
	
	//�f�o�b�O���[�h�ł��邩
	public static boolean isDebuggable;
	
	//Score
	public static int globalScore;
	
	//Area
	public static String globalArea;
	
	//Update flag
	public static int UpdateFlag = 0;
	
	//View onece
	public static int ViewOnce = 0;
	
	//http request result
	public static int HttpRequestResult = 0;
}
