package org.waremon.fissioin2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.util.Log;

public class WebAPI {
	/**
	 * DEBUG
	 */
	private static final boolean DEBUG = false;

	/**
	 * TAG
	 */
	private static final String TAG = "WEB_API";

	
	/**
	 * Bindするコンテキスト
	 */
	private Context mContext;

	/**
	 * Upload URL
	 */
	private static final String BASE_URL = "http://waremon-utils.appspot.com";  //自分のApplication Identifierを入れる
	
	/**
	 * Action ID of Upload
	 */
	public static final int ACT_UPLOAD = 1;
	/**
	 * Event Listener
	 */
	private WebAPIListener mWebAPIListener;
	
	public WebAPI(Context context) {
		this.mContext = context;
	}
	/**
	 * HTTP通信を行うクラス
	 * @param key
	 * @param value
	 */
	public void sendData(String[] key, String[] value){
		String url = BASE_URL + "/waremonutilsgae";  //サーブレットの名前
		PostThread mPostThread = new PostThread(ACT_UPLOAD, url ,key, value);
		mPostThread.start();
	}
	
	/**
	 * バックグラウンドで通信結果を取得するThreadクラス
	 *
	 */
	private class PostThread extends Thread {
		private String url;
		private int type;
		private List< NameValuePair > postParams;
		
		public PostThread(int type, String url,String[] key, String[] value) {
			this.url = url;
			this.type = type;
			postParams = new ArrayList< NameValuePair >(); 
			for(int i = 0; i < key.length; i++){
				postParams.add(new BasicNameValuePair(key[i],value[i]));
			}
		}

		public void run() {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);
			HttpClient mHttp = new DefaultHttpClient(httpParameters);

			try {
				HttpPost postMethod   = new HttpPost(url);
			
				// Header of Post
				postMethod.setHeader("Content-Type","application/x-www-form-urlencoded");
				
				// UrlEncode
				UrlEncodedFormEntity sendData = new UrlEncodedFormEntity(postParams, "UTF-8");
				postMethod.setEntity(sendData);
				
				// Connect
				HttpResponse mResponse = mHttp.execute(postMethod);
				if(DEBUG){
					Log.i(TAG,"connecting");
				}
				
				// Response Code
				int resCode = mResponse.getStatusLine().getStatusCode();
				// Response Type
				String resType = mResponse.getEntity().getContentType().getValue();
				// Response Value
				HttpEntity httpEntity = mResponse.getEntity();
				String resValue = EntityUtils.toString(httpEntity);
				
				if(DEBUG){
					Log.i(TAG,"resCode:"+ resCode);
					Log.i(TAG,"resType:"+ resType);
					Log.i(TAG,"resValue:"+ resValue);
				}
				
				// OK
				if (resCode == HttpStatus.SC_OK){
					mWebAPIListener.onLoad(type,resValue);
				}
				// NG
				else{	
					mWebAPIListener.onLoad(type,"-1");
				}
				
			} catch (IOException e) 
			{
				// NG
				mWebAPIListener.onLoad(type,"-1");
			}
		}
	}
	/**
	 * Eventリスナーを設定
	 * 
	 * @param listener
	 */
	public void setEventListener(WebAPIListener listener) {
		this.mWebAPIListener = listener;
	}
}