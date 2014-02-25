package com.redinfo.red4s.app;

import java.io.*;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import android.util.Log;

public final class Helper {
	private Helper() {
	}

	private static String BASE_URL = "http://fw.guotaiworld.com/index.php/interfaces/";
	// private static String BASE_URL =
	// "http://njcttq.bcm.cn/index.php/mobile/";
	// private static String BASE_URL = "http://www.youkang800.net/mobile/";
	// public static String LOGIN_URL = BASE_URL + "get_login_result";
	// public static String SEARCH_URL = BASE_URL + "get_barcode_result";
	// public static String INSPECT_URL = BASE_URL + "add_inspect_data";
	// public static String REG_URL = BASE_URL + "checkcode/";
	public static String AREA_URL = BASE_URL + "getarea";
	public static String LOGIN_URL = BASE_URL + "login";
	public static String REG_URL = BASE_URL + "checkcode/";
	public static String SEARCH_URL = BASE_URL + "tracing";
	public static String REPORT_URL = BASE_URL + "confirmresult";

	public static int ACCESS_SERVER_ERR = 0x910;
	public static int VERIFICATION_NEGATIVED = 0x00;
	public static int VERIFICATION_PASSED = 0x01;

	public static String HttpPost(String url, List<NameValuePair> params) {
		// TODO Auto-generated method stub

		/* 建立HTTP Post连线 */
		org.apache.http.client.methods.HttpPost httpRequest = new org.apache.http.client.methods.HttpPost(
				url);

		// Post运作传送变数必须用NameValuePair[]阵列储存
		// 传参数 服务端获取的方法为request.getParameter("name")
		// ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		// params.add(new BasicNameValuePair("name", "this is post"));

		try {
			// 发出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.ASCII));
			// 取得HTTP response
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			// 若状态码为200 ok
			// 取出回应字串
			int t = httpResponse.getStatusLine().getStatusCode();
			if (t == 200) {
				// Log.i("conn",conn+" ");
				// if (conn) {
				// 取出回应字串
				String strResult = EntityUtils.toString(httpResponse
						.getEntity());
				Log.v("strResult", strResult);
				// strResult += "";
				return strResult;
			} else {
				// Error
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String HttpGet(String url) {
		HttpClient client = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder();
		HttpGet myget = new HttpGet(url);
		try {
			HttpResponse response = client.execute(myget);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}

			char cr = 65279;
			String t = String.valueOf(cr);
			String resultString = builder.toString();
			resultString = resultString.replace("\t", "").replace(t, "");

			return resultString;

		} catch (Exception e) {
			Log.v("url response", "false");
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T HttpGet(String url, Class<T> classOfT) {
		String jsonString = HttpGet(url);
		// String jsonString =
		// HttpGet("http://123.235.2.94:9876/index.php/inspector/logistics/index/81265590044954066631");
		if (jsonString == null)
			return null;
		char cr = 65279;
		String t = String.valueOf(cr);
		jsonString = jsonString.replace("\t", "").replace(t, "");

		Gson g = new Gson();
		T result = null;
		try {
			result = g.fromJson(jsonString, classOfT);
			return result;
		} catch (Exception ex) {
			result = null;
		}
		return result;
	}

	public static <T> T HttpPost(String url, List<NameValuePair> params,
			Class<T> classOfT) {
		String jsonString = HttpPost(url, params);
		if (jsonString == null)
			return null;
		char cr = 65279;
		String t = String.valueOf(cr);
		jsonString = jsonString.replace("\t", "").replace(t, "");

		Gson g = new Gson();
		T result = null;
		try {
			result = g.fromJson(jsonString, classOfT);
			return result;
		} catch (Exception ex) {
			result = null;
		}
		return result;
	}
}