package com.dq.appcrashhandle.receiver;

import com.dq.appcrashhandle.CrashReportActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CrashReceiver extends BroadcastReceiver {

	private static final String TAG = "CrashReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("com.crash.test")) {
			Log.d(TAG, "received crash broadcast...");
			Intent i = new Intent(context, CrashReportActivity.class);
			i.getStringExtra("crash_info");
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}

}
