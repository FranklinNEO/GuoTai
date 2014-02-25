package com.redinfo.red4s.app;

//import com.baidu.mapapi.*;
import java.util.ArrayList;

import com.redinfo.red4s.datamodle.DataTable;
import com.redinfo.red4s.datamodle.DetailInfo;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.telephony.TelephonyManager;

public class BcmApplication extends Application {

	private TelephonyManager phonyManager = null;
	// 百度MapAPI的管理类
	// private BMapManager mapManager = null;

	String key = "592DA475D6A1B832132FB0EC798FAE227CD7FFDA";

	// public BMapManager getMapManager(){
	// if(this.mapManager == null)
	// this.initMapManager();
	// return this.mapManager;
	// }

	// public String googleLoc2baiduLoc(double lat, double lng){
	// String url =
	// String.format("http://api.map.baidu.com/ag/coord/convert?from=2&to=4&x=%f&y=%f",lat,
	// lng);
	// return Helper.HttpGet(url);
	// }

	// private void initMapManager(){
	// this.mapManager = new BMapManager(this);
	// this.mapManager.init(this.key, this);
	// }

	public String getDeviceId() {
		return this.getTelephonyManager().getDeviceId();
	}

	public String getSimSerialNumber() {
		return this.getTelephonyManager().getSimSerialNumber();
	}

	public String getSubscriberId() {
		return this.getTelephonyManager().getSubscriberId();
	}

	private TelephonyManager getTelephonyManager() {
		if (this.phonyManager == null) {
			this.phonyManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
		}
		return this.phonyManager;
	}

	private String userId = "";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String uid) {
		this.userId = uid;
	}

	public String pwd = "";

	public String getPwd() {
		return this.pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	private int position;

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return this.position;
	}

	private DetailInfo detailInfo = null;

	public DetailInfo getDetailInfo() {
		return detailInfo;
	}

	public void setDetialInfo(DetailInfo detail) {
		this.detailInfo = detail;
	}

	private DataTable[] searchResult = null;

	public DataTable[] getSearchResult() {
		return searchResult;
	}

	private String currentBarcode = null;

	public String getCurrentBarcode() {
		return currentBarcode;
	}

	public void setSearchResult(String barcode, ArrayList<DataTable> searchResult) {
		this.currentBarcode = barcode;
		this.searchResult = searchResult.toArray(new DataTable[]{});
	}

	private boolean isAutoFocus = false;

	public boolean getIsAutoFocus() {
		return this.isAutoFocus;
	}

	private Boolean IsAutoFocus() {
		String[] parameters = this.GetCamearParameters();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("focus-mode=auto"))
				return true;
		}
		return false;
	};

	private String[] GetCamearParameters() {
		Camera camera = Camera.open();
		Camera.Parameters parameters = camera.getParameters();
		String[] params = parameters.flatten().split(";");

		if (camera != null) {
			camera.release();
			camera = null;
		}
		return params;
	}

	@Override
	public void onCreate() {
		this.getTelephonyManager();
		// this.initMapManager();
		this.isAutoFocus = this.IsAutoFocus();
		super.onCreate();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		/*
		 * if (this.mapManager != null) { this.mapManager.destroy();
		 * this.mapManager = null; }
		 */
		super.onTerminate();
	}

	/*
	 * @Override public void onGetNetworkState(int iError) {
	 * Toast.makeText(this.getApplicationContext(), "您的网络出错啦！",
	 * Toast.LENGTH_LONG).show(); }
	 * 
	 * @Override public void onGetPermissionState(int iError) { if (iError ==
	 * MKEvent.ERROR_PERMISSION_DENIED) { // 授权Key错误：
	 * Toast.makeText(this.getApplicationContext(),
	 * "请在BMapApiDemoApp.java文件输入正确的授权Key！", Toast.LENGTH_LONG) .show(); } }
	 */
}