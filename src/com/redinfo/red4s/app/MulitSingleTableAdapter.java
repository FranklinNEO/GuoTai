package com.redinfo.red4s.app;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.redinfo.guotai.R;
import com.redinfo.red4s.datamodle.DataTable;
import com.redinfo.red4s.datamodle.DataRow.ItemPar;

public class MulitSingleTableAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private DataTable[] data;

	public MulitSingleTableAdapter(Context context, DataTable[] data) {
		this.inflater = LayoutInflater.from(context);
		this.data = data;
	}

	public int getCount() {
		return data.length;
	}

	public Object getItem(int arg0) {
		return null;
	}

	public void setDataTable(DataTable[] datatable) {
		data = datatable;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		DataTable currentData = this.data[position];
		convertView = new TableLayout(parent.getContext());
		if (currentData.getRowCount() < 1)
			return convertView;
		convertView = new TableLayout(parent.getContext());
		TableLayout tableView = ((TableLayout) convertView);

		LinearLayout newTitle = (LinearLayout) this.inflater.inflate(
				R.layout.title_item, null);
		newTitle.setBackgroundResource(R.drawable.title_bg);
		LinearLayout TitleName = (LinearLayout) newTitle.getChildAt(0);
		TextView titleTv = (TextView) TitleName.getChildAt(0);
		titleTv.setText(currentData.getTableName());
		tableView.addView(newTitle);


		for (int j = 0; j < currentData.getRowCount(); j++) {
			int columnCount = currentData.getRowAt(j).getItemCount();

			LinearLayout item = (LinearLayout) this.inflater.inflate(
					R.layout.table_item, null);
			LinearLayout itemContent = (LinearLayout) item.getChildAt(0);
			TextView titleContent = (TextView) itemContent.getChildAt(0);
			Paint paint = titleContent.getPaint();
			float textWidth = 0;
			for (int i = 0; i < columnCount; i++) {
				ItemPar iPar = this.data[position].getRowAt(j).getItemPar(i);
				float t = paint.measureText(iPar.getTitle() + "：");
				textWidth = t > textWidth ? t : textWidth;
			}

			for (int i = 0; i < columnCount; i++) {
				// 创建TableRow
				LinearLayout newRow = (LinearLayout) this.inflater.inflate(
						R.layout.table_item, null);
				newRow.setBackgroundResource(R.drawable.divider_bg);
				LinearLayout rowContent = (LinearLayout) newRow.getChildAt(0);
				// 创建标题
				TextView colTextView = (TextView) rowContent.getChildAt(0);

				// 创建内容
				TextView contentTextView = (TextView) rowContent.getChildAt(1);
				tableView.addView(newRow);

				ItemPar iPar = this.data[position].getRowAt(j).getItemPar(i);

				colTextView.setText(iPar.getTitle() + "：");
				colTextView.setWidth((int) textWidth);
				contentTextView.setText(iPar.getContent());
			}
			if ((currentData.getRowCount() > 1)
					&& (j < (currentData.getRowCount() - 1))) {
				TextView bottomView = new TextView(tableView.getContext());
				bottomView.setBackgroundResource(R.drawable.divider_bg);
				tableView.addView(bottomView);
			} else {
				TextView bottomView = new TextView(tableView.getContext());
				bottomView.setBackgroundResource(R.drawable.row_divider_bg);
				tableView.addView(bottomView);
			}
		}
		return convertView;
	}
}