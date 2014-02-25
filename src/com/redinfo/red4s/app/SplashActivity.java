package com.redinfo.red4s.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.redinfo.guotai.R;

public class SplashActivity extends Activity {
	private String pno = "";
	private String vCode = "";
	private String login = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.splash);
		FileInputStream inStream = null;
		ByteArrayOutputStream outStream = null;

		try {
			inStream = this.openFileInput("userInfo.txt");
			outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
			String content = outStream.toString();
			Log.v("content", content);
			if (content != null && content.indexOf(";") > 1) {
				pno = content.split(";")[0];
				vCode = content.split(";")[1];
				login = content.split(";")[2];
			}
			outStream.close();
			inStream.close();
		} catch (IOException ex) {
		}

		new Thread() {
			public void run() {
				try {
					sleep(750);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				check();
			}
		}.start();

		// RequestParams params = new RequestParams();
		// params.put("loginName", pno);
		// params.put("loginPwd", vCode);
		// AsyncHttpClient client = new AsyncHttpClient();
		// client.post(Helper.LOGIN_URL, params, new AsyncHttpResponseHandler()
		// {
		// @Override
		// public void onStart() {
		// }
		//
		// @Override
		// public void onFinish() {
		// }
		//
		// @Override
		// public void onSuccess(String content) {
		//
		// if (content != null && content.equalsIgnoreCase("1")) {
		// ((BcmApplication) getApplication()).setUserId(pno);
		// ((BcmApplication) getApplication()).setPwd(vCode);
		// Intent intent = new Intent();
		// intent.setClass(SplashActivity.this, SearchActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// SplashActivity.this.finish();
		// } else {
		// Intent intent = new Intent();
		//
		// intent.setClass(SplashActivity.this,
		// RegistrationActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// SplashActivity.this.finish();
		// }
		//
		// }
		//
		// @Override
		// public void onFailure(Throwable error) {
		// Toast.makeText(SplashActivity.this, "请检查您的网络设置",
		// Toast.LENGTH_LONG).show();
		// Log.i("onFailure", "failed");
		// }
		// });
	}

	private void check() {
		if (login != null && login.equalsIgnoreCase("1")) {
			((BcmApplication) this.getApplication()).setUserId(pno);
			((BcmApplication) this.getApplication()).setPwd(vCode);
			Intent intent = new Intent();
			intent.setClass(SplashActivity.this, InfoType.class);
			// intent.setClass(SplashActivity.this, SearchActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			SplashActivity.this.finish();
		} else {
			Intent intent = new Intent();
			intent.setClass(SplashActivity.this, RegistrationActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			SplashActivity.this.finish();
		}
	}

}
