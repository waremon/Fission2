package org.waremon.fissioin2;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Global {
	
	//MainActivity
	public static Fission2 mainActivity;
	//GLコンテキストを保持する変数
	public static GL10 gl;
	
	//ランダムな値を生成する
	public static Random rand =
			new Random(System.currentTimeMillis());
	
	//デバッグモードであるか
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
