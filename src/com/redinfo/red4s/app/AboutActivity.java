package com.redinfo.red4s.app;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.redinfo.guotai.R;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {
	private String name;
	private String verson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		Bundle bundle = this.getIntent().getExtras();
		name = bundle.getString("user_name");
		TextView tvName = (TextView) findViewById(R.id.loginfo);
		tvName.setText(name);
		String pkName = this.getPackageName();
		try {
			verson = this.getPackageManager().getPackageInfo(pkName, 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int year = Integer.parseInt(sdf.format(new Date()));
		TextView tvVerson = (TextView) findViewById(R.id.verson);
		tvVerson.setText(verson);
		TextView copyTv = (TextView) findViewById(R.id.copyright);
		copyTv.setText("Copyright © " + (year - 1) + "-" + year + " REDINFO.");
	}

}
