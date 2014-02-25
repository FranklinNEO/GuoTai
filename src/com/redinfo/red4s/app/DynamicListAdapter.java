package com.redinfo.red4s.app;

import java.util.ArrayList;

import com.redinfo.guotai.R;
import com.redinfo.red4s.datamodle.DataRow.ItemPar;
import com.redinfo.red4s.datamodle.DataTable;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DynamicListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private DataTable data;
	private Paint textPaint;

	public DynamicListAdapter(Context context, DataTable data) {
		this.inflater = LayoutInflater.from(context);
		this.data = data;

		LinearLayout tempRow = (LinearLayout) this.inflater.inflate(
				R.layout.table_item, null);
		TextView tempTextView = (TextView) ((LinearLayout) tempRow
				.getChildAt(0)).getChildAt(0);
		this.textPaint = tempTextView.getPaint();
	}

	public int getCount() {
		return data.getRowCount();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {

			holder = new ViewHolder();
			convertView = ((LinearLayout) this.inflater.inflate(
					R.layout.dynamic_list_item, null));
			LinearLayout tableView = ((LinearLayout) convertView);

			int columnCount = this.data.getRowAt(position).getItemCount();

			float maxWidth = -1;
			for (int i = 0; i < columnCount; i++) {
				ItemPar iPar = this.data.getRowAt(position).getItemPar(i);
				String text = iPar.getTitle() + "：";
				float t = this.textPaint.measureText(text);
				if (t > maxWidth)
					maxWidth = t;

				Log.v("maxWidth", String.valueOf(maxWidth));
			}

			for (int i = 0; i < columnCount; i++) {
				// 创建TableRow
				LinearLayout newRow = (LinearLayout) this.inflater.inflate(
						R.layout.table_item, null);
				// 设置行背景
				newRow.setBackgroundResource(R.drawable.cotent_bg);

				// 创建标题
				TextView colTextView = (TextView) ((LinearLayout) newRow
						.getChildAt(0)).getChildAt(0);
				colTextView.setWidth((int) maxWidth);
				// 创建内容
				TextView contentTextView = (TextView) ((LinearLayout) newRow
						.getChildAt(0)).getChildAt(1);
				tableView.addView(newRow);

				ItemPar iPar = this.data.getRowAt(position).getItemPar(i);

				colTextView.setText(iPar.getTitle() + "：");
				contentTextView.setText(iPar.getContent());
			}
			convertView.setTag(holder);

		} else {

			holder = (ViewHolder) convertView.getTag();

			int columnCount = this.data.getRowAt(position).getItemCount();
			for (int i = 0; i < columnCount; i++) {
				ItemPar iPar = this.data.getRowAt(position).getItemPar(i);
				holder.Titles.get(i).setText(iPar.getTitle());
				holder.Contents.get(i).setText(iPar.getContent());
			}
		}

		return convertView;
	}

	private class ViewHolder {
		public ArrayList<TextView> Titles = new ArrayList<TextView>();
		public ArrayList<TextView> Contents = new ArrayList<TextView>();
	}
}