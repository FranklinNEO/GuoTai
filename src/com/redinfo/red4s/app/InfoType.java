package com.redinfo.red4s.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.redinfo.guotai.R;
import com.redinfo.red4s.ui.CustomDialog;
import com.redinfo.red4s.datamodle.DetailInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class InfoType extends Activity implements OnClickListener {
	private Spinner provinceSp = null;
	private Spinner citySp = null;
	private Spinner countriesSp = null;
	private String[] defaultarray = { "请选择" };
	private String[] provincecode_array = { "" };
	private String[] provincename_array = { "" };
	private String[] citycode_array = { "" };
	private String[] cityname_array = { "" };
	private String[] countriescode_array = { "" };
	private String[] countriesname_array = { "" };
	public Dialog logindialog = null;
	private String areaCode = null;
	private DetailInfo detail = null;
	private Button nextBtn = null;
	private EditText RemarkEt = null;
	private static final int MENU_LOGOUT = Menu.FIRST;
	private static final int MENU_HISTORY = Menu.FIRST + 1;
	public static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.infotype);
		provinceSp = (Spinner) findViewById(R.id.provinceSpinner);
		citySp = (Spinner) findViewById(R.id.citySpinner);
		countriesSp = (Spinner) findViewById(R.id.countiesSpinner);
		RemarkEt = (EditText) findViewById(R.id.remark_et);
		nextBtn = (Button) findViewById(R.id.nextBtn);
		nextBtn.setOnClickListener(this);
		init();
		logindialog = new Dialog(InfoType.this, R.style.mmdialog);
		logindialog.setContentView(R.layout.loading_dialog);
		GetProvince();
		activity = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, MENU_LOGOUT, 0, "注销");
		menu.add(0, MENU_HISTORY, 0, "历史记录");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MENU_LOGOUT:
			Dialog dialog = null;
			CustomDialog.Builder customBuilder = new CustomDialog.Builder(
					InfoType.this);
			customBuilder
					.setMessage("确定要注销您的登录信息吗？")
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							})
					.setPositiveButton(" 退出登录 ",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										FileOutputStream outStream = InfoType.this
												.openFileOutput("userInfo.txt",
														Context.MODE_PRIVATE);
										String content = "" + ";" + "" + ";"
												+ "";
										outStream.write(content.getBytes());
										outStream.close();
									} catch (IOException ex) {

									}
									// File file = new File(URL,
									// CodeDBHelper.DATABASE_NAME);
									// db = SQLiteDatabase.openOrCreateDatabase(
									// file, null);
									// db.delete(CodeDBHelper.CODE_TABLE_NAME,
									// null, null);
									// db.close();
									Intent intent = new Intent(
											getApplication(),
											RegistrationActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									InfoType.this.finish();
									dialog.dismiss();
								}
							});
			dialog = customBuilder.create();
			dialog.show();
			return true;
		case MENU_HISTORY:
			Intent intent = new Intent();
			Bundle userBundle = new Bundle();
			userBundle.putString("username",
					((BcmApplication) this.getApplication()).getUserId());
			intent.putExtras(userBundle);
			intent.setClass(InfoType.this, HistoryList.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		this.provinceSp = (Spinner) this.findViewById(R.id.provinceSpinner);
		ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_item, defaultarray);
		this.provinceSp.setAdapter(provinceAdapter);
		this.provinceSp.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				provinceSp
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								int index = arg0.getSelectedItemPosition();
								if (index != 0) {
									GetCity(provincecode_array[index]);
								} else {
									areaCode = null;
								}
							}

							public void onNothingSelected(AdapterView<?> arg0) {
							}
						});
				return false;
			}

		});

		this.citySp = (Spinner) this.findViewById(R.id.citySpinner);
		ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_item, defaultarray);
		this.citySp.setAdapter(cityAdapter);
		this.citySp.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				citySp.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						int index = arg0.getSelectedItemPosition();

						if (index != 0 && (citycode_array.length > 1)) {
							areaCode = citycode_array[index];

						}

					}

					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});
				return false;
			}

		});

		this.countriesSp = (Spinner) this.findViewById(R.id.countiesSpinner);
		ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_item, defaultarray);
		this.countriesSp.setAdapter(countriesAdapter);
		this.countriesSp.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				countriesSp
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								int index = arg0.getSelectedItemPosition();
								if (index != 0 && (citycode_array.length > 1)) {
									areaCode = countriescode_array[index];
								}
							}

							public void onNothingSelected(AdapterView<?> arg0) {
							}
						});
				return false;
			}

		});
	}

	private void GetProvince() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("areaid", "");
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(Helper.AREA_URL, null, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				logindialog.show();
				Log.e("post", "onStart");
			}

			@Override
			public void onFinish() {
				logindialog.dismiss();
				Log.e("post", "onFinish");
			}

			@Override
			public void onSuccess(String content) {
				Log.e("onSuccess", content);
				ArrayList<String> provincecode_list = new ArrayList<String>();
				provincecode_list.add("-1");
				ArrayList<String> provincename_list = new ArrayList<String>();
				provincename_list.add("请选择");
				char cr = 65279;
				String t = String.valueOf(cr);
				content = content.replace("\t", "").replace(t, "");
				JSONObject obj;
				String error = "";
				String jsonString = "";
				try {
					obj = new JSONObject(content);
					jsonString = obj.getString("result");
					error = obj.getString("error");
					JSONArray jsonArray = new JSONArray(jsonString);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
						provincecode_list.add((jsonObject2.getString("areaId")));
						provincename_list.add((jsonObject2
								.getString("areaName")));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				provincecode_array = (String[]) provincecode_list
						.toArray(new String[provincecode_list.size()]);
				provincename_array = (String[]) provincename_list
						.toArray(new String[provincename_list.size()]);
				initprovinceSp();

			}

			@Override
			public void onFailure(Throwable error) {
				Log.e("onFailure", "failed");
			}
		});
	}

	protected void initprovinceSp() {
		// TODO Auto-generated method stub
		ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this,
				R.layout.simple_item, provincename_array);
		this.provinceSp.setAdapter(provinceAdapter);
	}

	private void GetCity(final String provincecode) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("areaid", provincecode);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(Helper.AREA_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				logindialog.show();
				Log.e("post", "onStart");
			}

			@Override
			public void onFinish() {
				logindialog.dismiss();
				Log.e("post", "onFinish");
			}

			@Override
			public void onSuccess(String content) {
				Log.e("onSuccess", content);
				ArrayList<String> citycode_list = new ArrayList<String>();
				citycode_list.add("-1");
				ArrayList<String> cityname_list = new ArrayList<String>();
				cityname_list.add("请选择");
				char cr = 65279;
				String t = String.valueOf(cr);
				content = content.replace("\t", "").replace(t, "");
				JSONObject obj;
				String error = "";
				String jsonString = "";
				try {
					obj = new JSONObject(content);
					jsonString = obj.getString("result");
					error = obj.getString("error");
					JSONArray jsonArray = new JSONArray(jsonString);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
						citycode_list.add((jsonObject2.getString("areaId")));
						cityname_list.add((jsonObject2.getString("areaName")));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				citycode_array = (String[]) citycode_list
						.toArray(new String[citycode_list.size()]);
				cityname_array = (String[]) cityname_list
						.toArray(new String[cityname_list.size()]);
				initcitySp(provincecode);
				initcountriesSp(null);
			}

			@Override
			public void onFailure(Throwable error) {
				Log.e("onFailure", "failed");
			}
		});
	}

	protected void initcitySp(String provincecode) {
		// TODO Auto-generated method stub
		// ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
		// R.layout.simple_item, cityname_array);
		// this.citySp.setAdapter(cityAdapter);

		// TODO Auto-generated method stub
		if (provincecode == null) {
			ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
					R.layout.simple_item, defaultarray);
			this.citySp.setAdapter(cityAdapter);
		} else {
			ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
					R.layout.simple_item, cityname_array);
			this.citySp.setAdapter(cityAdapter);
		}

		if (cityname_array.length == 1) {
			areaCode = provincecode;
		}

	}

	private void GetCountries(final String citycode) {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("areaid", citycode);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(Helper.AREA_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				logindialog.show();
				Log.e("post", "onStart");
			}

			@Override
			public void onFinish() {
				logindialog.dismiss();
				Log.e("post", "onFinish");
			}

			@Override
			public void onSuccess(String content) {
				Log.e("onSuccess", content);
				ArrayList<String> countriescode_list = new ArrayList<String>();
				countriescode_list.add("-1");
				ArrayList<String> countriesname_list = new ArrayList<String>();
				countriesname_list.add("请选择");
				char cr = 65279;
				String t = String.valueOf(cr);
				content = content.replace("\t", "").replace(t, "");
				JSONObject obj;
				String error = "";
				String jsonString = "";
				try {
					obj = new JSONObject(content);
					jsonString = obj.getString("result");
					error = obj.getString("error");
					JSONArray jsonArray = new JSONArray(jsonString);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
						countriescode_list.add((jsonObject2.getString("areaId")));
						countriesname_list.add((jsonObject2
								.getString("areaName")));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				countriescode_array = (String[]) countriescode_list
						.toArray(new String[countriescode_list.size()]);
				countriesname_array = (String[]) countriesname_list
						.toArray(new String[countriesname_list.size()]);
				initcountriesSp(citycode);
			}

			@Override
			public void onFailure(Throwable error) {
				Log.e("onFailure", "failed");
			}
		});
	}

	protected void initcountriesSp(String citycode) {
		// TODO Auto-generated method stub
		if (citycode == null) {
			ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(
					this, R.layout.simple_item, defaultarray);
			this.countriesSp.setAdapter(countriesAdapter);
		} else {
			ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(
					this, R.layout.simple_item, countriesname_array);
			this.countriesSp.setAdapter(countriesAdapter);
		}

		if (countriesname_array.length == 1) {
			areaCode = citycode;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Dialog dialog = null;
			CustomDialog.Builder customBuilder = new CustomDialog.Builder(
					InfoType.this);
			customBuilder
					.setMessage("确定要退出国台酒业物流稽查软件？")
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							})
					.setPositiveButton("退出稽查软件",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									finish();
								}
							});
			dialog = customBuilder.create();
			dialog.show();

			return true;

		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.nextBtn:
			try {
				if (areaCode.trim().equals("") || areaCode.trim() == null) {
					Toast.makeText(InfoType.this, "请选择区域位置", Toast.LENGTH_SHORT)
							.show();
				} else {
					detail = new DetailInfo();
					detail.setAreaCode(areaCode);
					String remark = RemarkEt.getText().toString();
					detail.setRemark(remark.trim());
					((BcmApplication) getApplication()).setDetialInfo(detail);
					Intent intent = new Intent();
					intent.setClass(InfoType.this, SearchActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			} catch (Exception e) {
				Toast.makeText(InfoType.this, "请完善位置信息", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		default:
			break;
		}
	}
}
