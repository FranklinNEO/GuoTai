package com.redinfo.red4s.datamodle;

import java.util.ArrayList;

public final class DataRow {
	private	ArrayList<ItemPar> datas = new ArrayList<ItemPar>();
    
	public void addNewData(String title, String content) {		
		this.datas.add(new ItemPar(title,content));
	}
	
	public int getItemCount() {
		return this.datas.size();
	}
	
	public ItemPar getItemPar(int index) {
		if(index>=0&&index<this.datas.size())
			return this.datas.get(index);
		return null;
	}
	
	public String getValueByColumnName(String col){
		if(this.datas.size()<1)
			return null;
		for(ItemPar item:this.datas){
			if(item.getTitle().equalsIgnoreCase(col)){
				return item.getContent();
			}
		}
		return null;
	}
	
	public class ItemPar {
		public ItemPar (String title, String content) {
			this.setTitle(title);
			this.setContent(content);
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTitle() {
			return this.title;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getContent() {
			return this.content;
		}
		private String title;
		private String content;
	}
}
