package com.redinfo.red4s.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.redinfo.red4s.datamodle.AreaInfo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

final class DBHelper {
  private String dbPathStr = null;
  private String dbFullNameStr = null;

  public DBHelper(Context context) {
    String packageName = context.getPackageName();
    dbPathStr = "data/data/" + packageName + "/database";
    dbFullNameStr = dbPathStr + "/bcm_db.db";

    try {
      File dbPath = new File(dbPathStr);
      File dbFullName = new File(dbFullNameStr);
      if (dbFullName.exists())
        return;
      if (!dbPath.exists()) {
        dbPath.mkdir();
      }
      dbFullName.createNewFile();

      InputStream assetsDB = context.getAssets().open("bcm_db.sqlite");
      OutputStream dbOut = new FileOutputStream(dbFullNameStr);

      byte[] buffer = new byte[1024];
      int length;
      while ((length = assetsDB.read(buffer)) > 0) {
        dbOut.write(buffer, 0, length);
      }
      dbOut.flush();
      dbOut.close();
      assetsDB.close();
    } catch (IOException ex) {

    }
  }

  // 获得所有省信息
  ArrayList<AreaInfo> getProvince() {
    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFullNameStr, null);

    if (db == null) {
      return null;
    }

    Cursor reader = db.rawQuery("select * from `areas` where length(`areas`.`code`)=2",
      null);
    if(reader == null){
      return null;
    }

    ArrayList<AreaInfo> result = null;
    while(reader.moveToNext()){

      if(result == null){
        result = new ArrayList<AreaInfo>();
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setName("请选择...");
        areaInfo.setCode("000000");
        result.add(areaInfo);
      }

      AreaInfo areaInfo = new AreaInfo();
      areaInfo.setCode(reader.getString(reader.getColumnIndex("code")));
      areaInfo.setName(reader.getString(reader.getColumnIndex("name")));
      result.add(areaInfo);
    }

    reader.close();
    db.close();
    return result;
  }

  // 获得所有市信息
  ArrayList<AreaInfo> getCityByProvinceCode(String code) {
    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFullNameStr, null);

    if (db == null) {
      return null;
    }

    String selectStr = "select * from `areas` where length(`areas`.`code`)=4" +
      " and substr(`areas`.`code`,1,2)=?";

    Cursor reader = db.rawQuery(selectStr, new String[]{code});
    if(reader == null){
      return null;
    }

    ArrayList<AreaInfo> result = null;
    while(reader.moveToNext()){
      if(result == null){
        result = new ArrayList<AreaInfo>();
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setName("请选择...");
        areaInfo.setCode("000000");
        result.add(areaInfo);
      }

      AreaInfo areaInfo = new AreaInfo();
      areaInfo.setCode(reader.getString(reader.getColumnIndex("code")));
      areaInfo.setName(reader.getString(reader.getColumnIndex("name")));
      result.add(areaInfo);
    }

    reader.close();
    db.close();
    return result;
  }

  // 获得所有县信息
  ArrayList<AreaInfo> getCountyByCityCode(String code) {
    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFullNameStr, null);

    if (db == null) {
      return null;
    }

    String selectStr = "select * from `areas` where length(`areas`.`code`)=6 " +
      " and substr(`areas`.`code`,1,4)=?";

    Cursor reader = db.rawQuery(selectStr, new String[] {code});
    if(reader == null){
      return null;
    }

    ArrayList<AreaInfo> result = null;
    while(reader.moveToNext()){
      if(result == null){
        result = new ArrayList<AreaInfo>();
        AreaInfo areaInfo = new AreaInfo();
        areaInfo.setName("请选择...");
        areaInfo.setCode("000000");
        result.add(areaInfo);
      }

      AreaInfo areaInfo = new AreaInfo();
      areaInfo.setCode(reader.getString(reader.getColumnIndex("code")));
      areaInfo.setName(reader.getString(reader.getColumnIndex("name")));
      result.add(areaInfo);
    }

    reader.close();
    db.close();
    return result;
  }
}