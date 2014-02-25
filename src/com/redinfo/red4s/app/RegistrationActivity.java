package com.redinfo.red4s.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.*;

import com.redinfo.guotai.R;
import com.redinfo.red4s.ui.PwdEditCancel;
import com.redinfo.red4s.ui.UserEditCancel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class RegistrationActivity extends Activity implements OnClickListener {

	private CheckBox saveUserInfoCbox = null;

	private Button regButton = null;

	private UserEditCancel ec1;
	private PwdEditCancel ec2;
	public Dialog logindialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.registration);
		this.saveUserInfoCbox = (CheckBox) this
				.findViewById(R.id.remember_user_checkbox);

		this.regButton = (Button) this
				.findViewById(R.id.registration_RegButton);
		ec2 = (PwdEditCancel) findViewById(R.id.pwd);
		ec1 = (UserEditCancel) findViewById(R.id.name);
		String usr = ec1.getString();
		String pwd = ec2.getString();

		if (usr != null && pwd != null) {
			this.regButton.setEnabled(true);
		} else {
			this.regButton.setEnabled(false);
		}

		this.regButton.setOnClickListener(this);

		logindialog = new Dialog(RegistrationActivity.this, R.style.mmdialog);
		logindialog.setContentView(R.layout.login_dialog);

	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.registration_RegButton:
			ec1 = (UserEditCancel) findViewById(R.id.name);
			String pno = ec1.getString().trim();
			ec2 = (PwdEditCancel) findViewById(R.id.pwd);
			String vCode = ec2.getString().trim();

			if (!Pattern.matches("^[A-Za-z0-9]+$", vCode)) {
				Toast.makeText(this, "您输入的密码有误,密码由0-9,和26位英文数字组成",
						Toast.LENGTH_LONG).show();
				break;
			}

			if (!Pattern.matches("^[A-Za-z0-9]+$", pno)) {
				Toast.makeText(this, "您输入的用户名有误,用户名由0-9,和26位英文数字组成",
						Toast.LENGTH_LONG).show();
				break;
			}
			RequestParams params = new RequestParams();
			// params.put("user_name", pno);
			// params.put("user_password", vCode);
			params.put("loginname", pno);
			params.put("loginpwd", vCode);
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(Helper.LOGIN_URL, params,
					new AsyncHttpResponseHandler() {
						@Override
						public void onStart() {
							logindialog.show();
							Log.d("RegistrationActivity", "onStart");
						}

						@Override
						public void onFinish() {
							logindialog.dismiss();
							Log.d("RegistrationActivity", "onFinish");
						}

						@Override
						public void onSuccess(String content) {
							Log.i("RegistrationActivity", "onSuccess");
							Log.i("onSuccess", content);
							
							byte[] c1 = content.getBytes();
							char cr = 65279;
							String t = String.valueOf(cr);
							content = content.replace("\t", "").replace(t, "");
							try {
								//JSONObject obj = new JSONObject("{'result':true,'error':''}");
								JSONObject obj=new JSONObject(content);
								boolean successful = obj.getBoolean("result");
								String error = obj.getString("error");
								if (successful) {
									Toast.makeText(RegistrationActivity.this,
											"登录成功", Toast.LENGTH_LONG).show();
									String usr = ec1.getString();
									String pwd = ec2.getString();
									saveUserInfo(usr.trim(), pwd.trim(),
											("1").trim());

									((BcmApplication) getApplication())
											.setUserId(usr.trim());
									((BcmApplication) getApplication())
											.setPwd(pwd.trim());
									Intent intent = new Intent(
											getApplication(), InfoType.class);
									// Intent intent=new
									// Intent(getApplication(),SearchActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									RegistrationActivity.this.finish();
								} else {
									Toast.makeText(RegistrationActivity.this,
											error, Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// if (content != null
							// && content.equalsIgnoreCase("1")) {
							// Log.d("onsuccess", "pass");
							// ec2 = (PwdEditCancel) findViewById(R.id.pwd);
							// ec1 = (UserEditCancel) findViewById(R.id.name);
							// String usr = ec1.getString();
							// String pwd = ec2.getString();
							// saveUserInfo(usr.trim(), pwd.trim(),
							// ("1").trim());
							//
							// ((BcmApplication) getApplication())
							// .setUserId(usr.trim());
							// ((BcmApplication) getApplication()).setPwd(pwd
							// .trim());
							// Intent intent;
							// intent = new Intent(getApplication(),
							// SearchActivity.class);
							// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							// startActivity(intent);
							// RegistrationActivity.this.finish();
							//
							// } else {
							// Log.d("onsuccess", "reject");
							// Toast.makeText(RegistrationActivity.this,
							// "登陆失败!请确定您输入的信息正确.", Toast.LENGTH_LONG)
							// .show();
							// }
						}

						@Override
						public void onFailure(Throwable error) {
							Toast.makeText(RegistrationActivity.this,
									"网络连接出错.", Toast.LENGTH_LONG).show();
							Log.i("onFailure", "failed");
						}
					});

			break;
		default:
			break;
		}
	}

	public void saveUserInfo(String uid, String pwd, String login) {
		if (!this.saveUserInfoCbox.isChecked())
			return;
		try {
			FileOutputStream outStream = this.openFileOutput("userInfo.txt",
					Context.MODE_PRIVATE);
			String content = uid + ";" + pwd + ";" + login;
			outStream.write(content.getBytes());
			outStream.close();
		} catch (IOException ex) {

		}
	}
}