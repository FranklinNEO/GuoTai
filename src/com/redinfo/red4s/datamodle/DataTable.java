package com.redinfo.red4s.datamodle;

import java.util.ArrayList;

public final class DataTable {
	public DataTable() {
		this.rows = new ArrayList<DataRow>();
	}

	public DataTable(String tableName) {
		this.rows = new ArrayList<DataRow>();
		this.setTableName(tableName);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public DataRow getRowAt(int index) {
		return this.rows.get(index);
	}

	public int getRowCount() {
		return this.rows.size();
	}

	public void addNewRow(DataRow newRow) {
		if (newRow == null)
			return;
		this.rows.add(newRow);
	}

	private String tableName;
	private ArrayList<DataRow> rows;
}