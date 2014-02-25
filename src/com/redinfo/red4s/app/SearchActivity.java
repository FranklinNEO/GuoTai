package com.redinfo.red4s.app;

import android.content.Context;
import mexxen.mx5010.barcode.*;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.redinfo.guotai.R;
import com.redinfo.red4s.data.CodeDBHelper;
import com.redinfo.red4s.ui.CustomDialog;
import com.redinfo.red4s.barcode.CaptureActivity;
import com.redinfo.red4s.datamodle.DataRow;
import com.redinfo.red4s.datamodle.DataTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public final class SearchActivity extends ListActivity {
	private static final int BARCODE_INTENT_REQ_CODE = 0x965;
	public final static String URL = "/data/data/com.redinfo.guotai/databases";
	private static final String BARCODE_SCANER_INTENT = "com.redinfo.red4s.barcode.SCAN";

	SQLiteDatabase db = null;
	public CodeDBHelper m_db = null;
	public Dialog querydialog = null;
	public Dialog logoutdialog = null;

	private Button searchButton = null;
	private Button scanButton = null;

	private ListView listView = null;
	private MulitSingleTableAdapter listItemAdapter = null;
	private EditText barcodeEditText = null;

	private static final int MENU_HISTORY = Menu.FIRST;
	private static final int MENU_ABOUT = Menu.FIRST + 1;
	private static final int MENU_LOGOUT = Menu.FIRST + 2;

	private String reString = null;
	private String Result_code = null;
	private String Result_content = null;
	private int Result_flag = 0;
	private String path = null;

	// private BarcodeManager bm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search);
		m_db = CodeDBHelper.getInstance(SearchActivity.this);
		File file = new File(URL, CodeDBHelper.DATABASE_NAME);
		db = SQLiteDatabase.openOrCreateDatabase(file, null);

		this.scanButton = (Button) this.findViewById(R.id.scanButton);
		this.barcodeEditText = (EditText) findViewById(R.id.code);
		this.searchButton = (Button) this
				.findViewById(R.id.search_searchButton);

		// bm = new BarcodeManager(this);
		// 添加扫描事件监听
		// bm.addListener(new BarcodeListener() {
		// // 重写barcodeEvent 方法，获取条码事件
		// public void barcodeEvent(BarcodeEvent event) {
		// // 当条码事件的命令为“SCANNER_READ”时，进行操作
		// if (event.getOrder().equals("SCANNER_READ")) {
		// // 调用getBarcode()方法读取条码信息
		// String barcode = bm.getBarcode();
		// SearchActivity.this.barcodeEditText.setText(barcode);
		// searchBarcode(barcode);
		// }
		// }
		// });

		// TextView itv = (TextView) findViewById(R.id.itv);
		// ImageGetter imageGetter = new ImageGetter() {
		// public Drawable getDrawable(String source) {
		// int id = Integer.parseInt(source);
		//
		// Drawable d = getResources().getDrawable(id);
		// d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		// return d;
		// }
		// };
		// itv.append(Html.fromHtml(
		// "  <img src=\"" + R.drawable.device_access_camera
		// + "\"> 启动手机摄像头拍摄条码快速查询", imageGetter, null));
		// this.imm = (InputMethodManager)
		// getSystemService(INPUT_METHOD_SERVICE);

		this.listView = this.getListView();
		// this.emptyTextView = (TextView)
		// this.findViewById(android.R.id.empty);

		querydialog = new Dialog(SearchActivity.this, R.style.mmdialog);
		querydialog.setContentView(R.layout.query_dialog);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {

			Result_code = bundle.getString("result_code");
			Result_content = bundle.getString("result_content");
			Result_flag = bundle.getInt("result_flag");
			Log.d("Result_code", Result_code);
			Log.d("Result_content", Result_content);
			this.barcodeEditText.setText(Result_code);
			if (Result_flag == 1) {
				DataTable[] result = null;
				String jsonString = Result_content;
				if (jsonString == null)
					result = null;
				char cr = 65279;
				String t = String.valueOf(cr);
				jsonString = jsonString.replace("\t", "").replace(t, "");

				Gson g = new Gson();
				try {
					result = g.fromJson(jsonString, DataTable[].class);
				} catch (Exception ex) {
					result = null;
				}
				// if (result != null) {
				// ((BcmApplication) SearchActivity.this.getApplication())
				// .setSearchResult(barcodeEditText.getText().toString(),
				// result);
				// } else {
				// reString = Result_content;
				// ((BcmApplication) SearchActivity.this.getApplication())
				// .setSearchResult(barcodeEditText.getText().toString(),
				// getDatas());
				// }
				ShowInfo(result);
			} else if (Result_flag == 0) {
				reString = Result_content;
				ShowInfo(getDatas().toArray(new DataTable[] {}));
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, MENU_HISTORY, 0, "历史记录");
		menu.add(0, MENU_ABOUT, 0, "关于");
		menu.add(0, MENU_LOGOUT, 0, "退出");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MENU_LOGOUT:
			// Dialog dialog = null;
			// CustomDialog.Builder customBuilder = new CustomDialog.Builder(
			// SearchActivity.this);
			// customBuilder
			// .setMessage("确定要注销您的登录信息吗？")
			// .setNegativeButton("取消",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int which) {
			// dialog.dismiss();
			// }
			// })
			// .setPositiveButton(" 退出登录 ",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int which) {
			// try {
			// FileOutputStream outStream = SearchActivity.this
			// .openFileOutput("userInfo.txt",
			// Context.MODE_PRIVATE);
			// String content = "" + ";" + "" + ";"
			// + "";
			// outStream.write(content.getBytes());
			// outStream.close();
			// } catch (IOException ex) {
			//
			// }
			// // File file = new File(URL,
			// // CodeDBHelper.DATABASE_NAME);
			// // db = SQLiteDatabase.openOrCreateDatabase(
			// // file, null);
			// // db.delete(CodeDBHelper.CODE_TABLE_NAME,
			// // null, null);
			// // db.close();
			// Intent intent = new Intent(
			// getApplication(),
			// RegistrationActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
			// SearchActivity.this.finish();
			// dialog.dismiss();
			// }
			// });
			// dialog = customBuilder.create();
			// dialog.show();

			Dialog dialog = null;
			CustomDialog.Builder customBuilder = new CustomDialog.Builder(
					SearchActivity.this);
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
									InfoType.activity.finish();
									finish();
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
			intent.setClass(SearchActivity.this, HistoryList.class);
			startActivity(intent);
			return true;

		case MENU_ABOUT:
			String strName = ((BcmApplication) this.getApplication())
					.getUserId();
			Intent dataIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("user_name", strName);
			dataIntent.putExtras(bundle);
			dataIntent.setClass(SearchActivity.this, AboutActivity.class);
			startActivity(dataIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public ArrayList<DataTable> getDatas() {
		DataTable t1 = new DataTable();
		t1.setTableName("没有查询到相关数据");
		DataRow t1R = new DataRow();
		reString = reString.replaceAll("\"", "");
		t1R.addNewData("注意", reString);
		t1.addNewRow(t1R);
		ArrayList<DataTable> r = new ArrayList<DataTable>();
		r.add(t1);
		final int size = r.size();
		DataTable[] result = new DataTable[size];
		// r.toArray(result);
		return r;
	}

	public ArrayList<DataTable> getNoDatas() {
		DataTable t1 = new DataTable();
		t1.setTableName("没有查询到相关数据");
		DataRow t1R = new DataRow();
		t1R.addNewData("注意", "获取服务器数据失败!");
		t1.addNewRow(t1R);
		ArrayList<DataTable> r = new ArrayList<DataTable>();
		r.add(t1);
		final int size = r.size();
		DataTable[] result = new DataTable[size];
		// r.toArray(result);
		return r;
	}

	public void onSearchButtonClick(View view) {
		String barcode = this.barcodeEditText.getText().toString();
		path = null;
		try {
			this.searchBarcode(barcode);
		} catch (Exception e) {
		}
	}

	public void onScanButtonClick(View view) {
		this.openScanner();
	}

	private void openScanner() {
		Intent intent = new Intent(BARCODE_SCANER_INTENT);
		intent.setClass(SearchActivity.this, CaptureActivity.class);
		this.startActivityForResult(intent, BARCODE_INTENT_REQ_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == BARCODE_INTENT_REQ_CODE) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				path = intent.getStringExtra("PIC_PATH");
				Log.d("path", path);
				// byte b[] = contents.getBytes();
				// byte b[]=hexStringToBytes(contents);
				// this.barcodeEditText.setText(contents);
				String prefixStr = contents.substring(0, 27);
				char begin = contents.charAt(26);
				char end = contents.charAt(39);
				if (contents.length() == 40
						& (prefixStr.equals("http://fw.guotaiworld.com/S"))
						& (contents.endsWith("D"))) {
					String str = contents.substring(27, 39);
					try {
						this.searchBarcode(str);
					} catch (Exception e) {

					}
				} else {
					Toast.makeText(this, "非有效条码，请查实！", Toast.LENGTH_LONG)
							.show();
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "未找到条形码", Toast.LENGTH_LONG).show();
			}
		}
	}

	private void searchBarcode(final String barcode) {
		this.barcodeEditText.setText(barcode);
		if (barcode.length() == 0) {
			Toast.makeText(this, "请输入所要查询的条码!", Toast.LENGTH_LONG).show();
			return;
		} else if (barcode.length() != 12) {
			Toast.makeText(this, "您输入的不是有效条码，请查实！", Toast.LENGTH_LONG).show();
			return;
		}
		String uid = ((BcmApplication) this.getApplication()).getUserId();
		String pwd = ((BcmApplication) this.getApplication()).getPwd();
		String areacode = ((BcmApplication) this.getApplication())
				.getDetailInfo().getAreaCode();
		String remark = ((BcmApplication) this.getApplication())
				.getDetailInfo().getRemark();
		RequestParams params = new RequestParams();

		params.put("loginname", uid);
		params.put("loginpwd", pwd);
		params.put("code", barcode);
		params.put("areaid", areacode);
		params.put("memo", remark);
		Log.i("loginname", uid);
		Log.i("loginpwd", pwd);
		Log.i("code", barcode);

		AsyncHttpClient client = new AsyncHttpClient();
		client.post(Helper.SEARCH_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				// SearchActivity.this.scanButton.setEnabled(false);
				// SearchActivity.this.searchButton.setEnabled(false);
				querydialog.show();
			}

			@Override
			public void onFinish() {
				querydialog.dismiss();
				// SearchActivity.this.scanButton.setEnabled(true);
				// SearchActivity.this.searchButton.setEnabled(true);
				ShowInfo(((BcmApplication) SearchActivity.this.getApplication())
						.getSearchResult());

			}

			@Override
			public void onSuccess(String content) {
				Log.i("SearchResult", content);
				// DataTable[] result = null;
				char cr = 65279;
				String t = String.valueOf(cr);
				content = content.replace("\t", "").replace(t, "");
				JSONObject obj;
				String error = "";
				String jsonString = "";
				ArrayList<DataTable> result = null;
				try {
					obj = new JSONObject(content);
					jsonString = obj.getString("result");
					error = obj.getString("error");
					if (jsonString == null)
						result = null;
					char cr1 = 65279;
					String t1 = String.valueOf(cr1);
					jsonString = jsonString.replace("\t", "").replace(t1, "");
					Gson g = new Gson();
					result = g.fromJson(jsonString,
							new TypeToken<ArrayList<DataTable>>() {
							}.getType());
				} catch (Exception ex) {
					result = null;
				}
				if (result != null) {
					((BcmApplication) SearchActivity.this.getApplication())
							.setSearchResult(barcodeEditText.getText()
									.toString(), result);
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd   HH:mm:ss");
					String CodeDate = df.format(new java.util.Date());
					m_db.insert_code(CodeDBHelper.CODE_TABLE_NAME,
							((BcmApplication) SearchActivity.this
									.getApplication()).getUserId(),
							barcodeEditText.getText().toString(), jsonString,
							path, CodeDate, 1);
					Log.i("onSuccess", "return");
					// Submmit(barcode);
				} else {
					reString = error;
					((BcmApplication) SearchActivity.this.getApplication())
							.setSearchResult(barcodeEditText.getText()
									.toString(), getDatas());
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd   HH:mm:ss");
					String CodeDate = df.format(new java.util.Date());
					m_db.insert_code(CodeDBHelper.CODE_TABLE_NAME,
							((BcmApplication) SearchActivity.this
									.getApplication()).getUserId(),
							barcodeEditText.getText().toString(), error, path,
							CodeDate, 0);
					Log.i("onSuccess", "null");
				}
			}

			@Override
			public void onFailure(Throwable error) {
				((BcmApplication) SearchActivity.this.getApplication())
						.setSearchResult(barcodeEditText.getText().toString(),
								getNoDatas());
				Log.i("onFailure", "failed");
			}
		});

	}

	protected void Submmit(String barcode) {
		// TODO Auto-generated method stub
		String uid = ((BcmApplication) this.getApplication()).getUserId();
		String pwd = ((BcmApplication) this.getApplication()).getPwd();
		String areacode = ((BcmApplication) this.getApplication())
				.getDetailInfo().getAreaCode();
		String remark = ((BcmApplication) this.getApplication())
				.getDetailInfo().getRemark();
		RequestParams params = new RequestParams();
		params.put("user_name", uid);
		params.put("user_password", pwd);
		params.put("barcode", barcode);
		params.put("area_code", areacode);
		params.put("remark", remark);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(Helper.SEARCH_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				Log.e("post", "onStart");
				Toast.makeText(SearchActivity.this, "正在提交查询记录",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				Log.e("post", "onFinish");

			}

			@Override
			public void onSuccess(String content) {
				Log.e("onSuccess", content);
				char cr = 65279;
				String t = String.valueOf(cr);
				content = content.replace("\t", "").replace(t, "");
				JSONObject obj;
				try {
					obj = new JSONObject(content);
					boolean successful = obj.getBoolean("successful");
					String error = obj.getString("error");
					if (successful) {
						Toast.makeText(SearchActivity.this, "提交成功!",
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(SearchActivity.this, error,
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(Throwable error) {
				Log.e("onFailure", "failed");
				Toast.makeText(SearchActivity.this, "网络连接错误",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void ShowInfo(DataTable[] dataTables) {
		// TODO Auto-generated method stub
		if (dataTables == null) {
			this.FillDynamicList(getDatas().toArray(new DataTable[] {}));
			return;
		}
		this.FillDynamicList(dataTables);
	}

	private void FillDynamicList(DataTable[] data) {
		// TODO Auto-generated method stub
		if (listView == null || data == null) {
			Log.i("search_list", "null");
			return;
		}

		if (listView != null) {
			// this.listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
			// this.listView.setDividerHeight(9);
			// this.listView.setScrollingCacheEnabled(false);
			// this.listView.setFadingEdgeLength(0);
			Log.i("search_list", "not null");
		}
		listItemAdapter = new MulitSingleTableAdapter(this, data);
		this.listView.setAdapter(listItemAdapter);
		Log.i("search_list", "adapter");
		SearchActivity.this.listItemAdapter.setDataTable(data);
		SearchActivity.this.listItemAdapter.notifyDataSetChanged();

	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// // Dialog dialog = null;
	// // CustomDialog.Builder customBuilder = new CustomDialog.Builder(
	// // SearchActivity.this);
	// // customBuilder
	// // .setMessage("确定要退出国台酒业物流稽查软件？")
	// // .setNegativeButton("取消",
	// // new DialogInterface.OnClickListener() {
	// // public void onClick(DialogInterface dialog,
	// // int which) {
	// // dialog.dismiss();
	// // }
	// // })
	// // .setPositiveButton("退出稽查软件",
	// // new DialogInterface.OnClickListener() {
	// // public void onClick(DialogInterface dialog,
	// // int which) {
	// // dialog.dismiss();
	// // finish();
	// // }
	// // });
	// // dialog = customBuilder.create();
	// // dialog.show();
	//
	// return true;
	//
	// } else {
	// return super.onKeyDown(keyCode, event);
	// }

	// }

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}