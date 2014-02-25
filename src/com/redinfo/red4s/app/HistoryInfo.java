package com.redinfo.red4s.app;

import java.io.File;
import java.util.ArrayList;

import com.redinfo.guotai.R;
import com.redinfo.red4s.data.CodeDBHelper;
import com.redinfo.red4s.datamodle.DataRow;
import com.redinfo.red4s.datamodle.DataTable;
import com.google.gson.Gson;

import android.app.Dialog;
import android.app.ListActivity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryInfo extends ListActivity {
	public final static String URL = "/data/data/com.redinfo.guotai/databases";
	SQLiteDatabase db = null;
	public CodeDBHelper m_db = null;
	public Dialog querydialog = null;
	public Dialog logoutdialog = null;

	// private TextView emptyTextView = null;
	private ListView listView = null;
	private MulitSingleTableAdapter listItemAdapter = null;
	private TextView barcodeEditText = null;

	private String reString = null;
	private String Result_code = null;
	private String Result_content = null;
	private int Result_flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.history_info);

		m_db = CodeDBHelper.getInstance(HistoryInfo.this);
		File file = new File(URL, CodeDBHelper.DATABASE_NAME);
		db = SQLiteDatabase.openOrCreateDatabase(file, null);

		this.barcodeEditText = (TextView) findViewById(R.id.info_code);

		this.listView = this.getListView();

		querydialog = new Dialog(HistoryInfo.this, R.style.mmdialog);
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
				ShowInfo(getDatas());
			}

		}
		db.close();
	}

	public DataTable[] getDatas() {
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
		r.toArray(result);
		return result;
	}

	public DataTable[] getNoDatas() {
		DataTable t1 = new DataTable();
		t1.setTableName("没有查询到相关数据");
		DataRow t1R = new DataRow();
		t1R.addNewData("注意", "获取服务器数据失败!");
		t1.addNewRow(t1R);
		ArrayList<DataTable> r = new ArrayList<DataTable>();
		r.add(t1);
		final int size = r.size();
		DataTable[] result = new DataTable[size];
		r.toArray(result);
		return result;
	}

	protected void ShowInfo(DataTable[] dataTables) {
		// TODO Auto-generated method stub
		if (dataTables == null) {
			this.FillDynamicList(getDatas());
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
		HistoryInfo.this.listItemAdapter.setDataTable(data);
		HistoryInfo.this.listItemAdapter.notifyDataSetChanged();

	}

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