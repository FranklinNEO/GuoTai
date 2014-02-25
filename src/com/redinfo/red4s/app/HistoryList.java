package com.redinfo.red4s.app;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.redinfo.guotai.R;
import com.redinfo.red4s.data.CodeDBHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class HistoryList extends Activity implements OnItemLongClickListener,
		OnClickListener, OnItemClickListener {
	public final static String URL = "/data/data/com.redinfo.guotai/databases";
	private ArrayList<HashMap<String, String>> barcode = new ArrayList<HashMap<String, String>>();
	SQLiteDatabase db = null;
	public CodeDBHelper m_db = null;
	private ListView list = null;
	private Button clear = null;
	private String sql = null;
	private CustomAdapter adapter;
	private static final int MENU_CLEAR = Menu.FIRST;
	public boolean[] itemStatus;
	public Dialog loadingdialog = null;
	private String username = null;
	private ImageLoader mImageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.history);
		m_db = CodeDBHelper.getInstance(HistoryList.this);
		list = (ListView) findViewById(R.id.historylist);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		clear = (Button) findViewById(R.id.deleteButton);
		clear.setOnClickListener(this);
		loadingdialog = new Dialog(HistoryList.this, R.style.mmdialog);
		loadingdialog.setContentView(R.layout.loading_dialog);
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error).cacheInMemory()
				.cacheOnDisc().displayer(new RoundedBitmapDisplayer(20))
				.build();
		Bundle bundle = this.getIntent().getExtras();
		username = bundle.getString("username");
		new AsyncTask<Integer, Integer, String[]>() {

			protected void onPreExecute() {
				loadingdialog.show();
				super.onPreExecute();
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
			}

			protected String[] doInBackground(Integer... params) {
				File file = new File(URL, CodeDBHelper.DATABASE_NAME);
				db = SQLiteDatabase.openOrCreateDatabase(file, null);
				sql = "SELECT * FROM code_date WHERE username='" + username
						+ "';";
				Cursor cur = db.rawQuery(sql, null);
				barcode = new ArrayList<HashMap<String, String>>();
				if (cur != null && cur.moveToFirst()) {
					do {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("barcode",
								cur.getString(cur.getColumnIndex("code20")));
						map.put("content",
								cur.getString(cur.getColumnIndex("content")));
						map.put("path",
								cur.getString(cur.getColumnIndex("picPath")));
						map.put("time",
								cur.getString(cur.getColumnIndex("createTime")));
						map.put("flag", cur.getInt(cur.getColumnIndex("flag"))
								+ "");
						barcode.add(map);
					} while ((cur.moveToNext()));
					cur.close();
					db.close();
				} else {
					cur.close();
					db.close();
				}
				Collections.reverse(barcode);
				return null;
			}

			protected void onPostExecute(String[] result) {
				itemStatus = new boolean[barcode.size()];
				adapter = new CustomAdapter(HistoryList.this);
				list.setAdapter(adapter);
				loadingdialog.dismiss();
				super.onPostExecute(result);
			}
		}.execute(0);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_CLEAR, 0, "清除全部");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_CLEAR:
			// File file = new File(URL, CodeDBHelper.DATABASE_NAME);
			// db = SQLiteDatabase.openOrCreateDatabase(file, null);
			// db.delete(CodeDBHelper.CODE_TABLE_NAME, null, null);
			m_db.delete_user(CodeDBHelper.CODE_TABLE_NAME, username);
			barcode.clear();
			adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public final class ViewHolder {
		public TextView Barcode;
		public TextView Time;
		public CheckBox check;
		public ImageButton im;
	}

	// 自定义条码数据容纳器
	private class CustomAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

		public CustomAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return barcode.size();
		}

		public Object getItem(int position) {
			return barcode.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			// ((BcmApplication) HistoryList.this.getApplication())
			// .setPosition(position);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.history_item, null);
				holder = new ViewHolder();
				holder.check = (CheckBox) convertView
						.findViewById(R.id.checkBoxEdit);
				holder.Barcode = (TextView) convertView
						.findViewById(R.id.barcodetTv);
				holder.Time = (TextView) convertView
						.findViewById(R.id.createtimeTv);
				holder.im = (ImageButton) convertView
						.findViewById(R.id.code_image);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.Barcode.setText(barcode.get(position).get("barcode"));
			holder.Time.setText(barcode.get(position).get("time"));
			holder.check
					.setOnCheckedChangeListener(new MyCheckBoxChangedListener(
							position));
			holder.check.setChecked(itemStatus[position]);
			holder.im.setFocusable(false);
			if (barcode.get(position).get("path") != null) {
				// Options options = new Options();
				// options.inSampleSize = 32;
				// Bitmap bitmap =
				// BitmapFactory.decodeFile(barcode.get(position)
				// .get("path"), options);

				// holder.im.setImageBitmap(BitmapFactory.decodeFile(barcode.get(
				// position).get("path")));
				Log.d("path", barcode.get(position).get("path"));
				mImageLoader.displayImage("file://"
						+ barcode.get(position).get("path"), holder.im,
						options, animateFirstListener);
			} else {
//				try {
//					final float scale = HistoryList.this.getResources()
//							.getDisplayMetrics().density;
//					LayoutParams p = new LayoutParams(
//							(int) (50 * scale + 0.5f),
//							(int) (50 * scale + 0.5f));
//					holder.im.setLayoutParams(p);
//					holder.im.setImageBitmap(Create2DCode(barcode.get(position)
//							.get("barcode")));
//					// mImageLoader.displayImage(null, holder.im, options,
//					// animateFirstListener);
//				} catch (WriterException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				mImageLoader.displayImage("drawable://" + R.drawable.ic_empty,
 holder.im, options, animateFirstListener);
			}
			holder.im.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent pic_intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("code_pic",
							barcode.get(position).get("path"));
					String code = barcode.get(position).get("barcode");
					bundle.putString("code", code);
					pic_intent.putExtras(bundle);
					pic_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					pic_intent.setClass(HistoryList.this, ShowCodePic.class);
					startActivity(pic_intent);
				}
			});
			return convertView;
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	class MyCheckBoxChangedListener implements OnCheckedChangeListener {
		int position;

		MyCheckBoxChangedListener(int position) {
			this.position = position;
		}

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked)
				itemStatus[position] = true;
			else
				itemStatus[position] = false;
		}
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.deleteButton:
			File file = new File(URL, CodeDBHelper.DATABASE_NAME);
			db = SQLiteDatabase.openOrCreateDatabase(file, null);
			for (int i = barcode.size() - 1; i >= 0; i--) {
				if (itemStatus[i]) {
					m_db.delete_code(CodeDBHelper.CODE_TABLE_NAME,
							barcode.get(i).get("barcode"),
							barcode.get(i).get("time"));
					barcode.remove(i);
//					if(barcode.get(i).get("path")!=null){
//						File pic=new File(barcode.get(i).get("path"));
//						pic.delete();
//					}
				}
			}
			itemStatus = new boolean[barcode.size()];
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		int i = Integer.parseInt(barcode.get(arg2).get("flag"));
		Intent ResultStr = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("result_code", barcode.get(arg2).get("barcode"));
		bundle.putString("result_content", barcode.get(arg2).get("content"));
		bundle.putInt("result_flag", i);
		ResultStr.putExtras(bundle);
		// ResultStr.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// ResultStr.setClass(HistoryList.this, SearchActivity.class);
		ResultStr.setClass(HistoryList.this, HistoryInfo.class);
		startActivity(ResultStr);
		// HistoryList.this.finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// if (barcode.size() == 0) {
		// Intent reset = new Intent();
		// reset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// reset.setClass(HistoryList.this, SearchActivity.class);
		// startActivity(reset);
		// }
		HistoryList.this.finish();
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Intent pic_intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("code_pic", barcode.get(arg2).get("path"));
		String code = barcode.get(arg2).get("barcode");
		bundle.putString("code", code);
		pic_intent.putExtras(bundle);
		pic_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pic_intent.setClass(HistoryList.this, ShowCodePic.class);
		startActivity(pic_intent);
		return false;
	}

	public Bitmap Create2DCode(String str) throws WriterException {
		// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode("110000097483",
				BarcodeFormat.QR_CODE, 300, 300);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap,具体参考api
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

}
