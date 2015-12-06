package com.dq.appcrashhandle.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.dq.appcrashhandle.CrashReportActivity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class CrashReportService extends Service {

	protected static final String TAG = "CrashReportService";
	private static Context mContext;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
	}

	public CrashReportService getInstance() {
		return this;
	}
	
	public static void reportCrash(String fileName) {
		Log.d(TAG, "received msg from CrashHandler: file =" + fileName);
		fileName = Environment.getExternalStorageDirectory().getPath() + "/crash/log/" +fileName;
		String crashInfo = readCrashInfo(fileName);
		
		Log.d(TAG, "crashInfo: " + crashInfo);
		Intent i = new Intent(mContext, CrashReportActivity.class);
		i.putExtra("crash_info", crashInfo);
		i.putExtra("filePath", fileName);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(i);
	}
	
	private static String readCrashInfo(String fileName) {
		Log.d(TAG, "-----" + fileName);
		File file = new File(fileName);
		StringBuffer sb = new StringBuffer();
		String s = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((s = reader.readLine()) != null) {
				sb.append(s);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
}