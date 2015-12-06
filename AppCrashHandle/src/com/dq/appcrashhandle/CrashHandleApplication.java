package com.dq.appcrashhandle;

import android.app.Application;

public class CrashHandleApplication extends Application {

	private static CrashHandleApplication sInstance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		
	}
	
	public static CrashHandleApplication getInstance() {
		return sInstance;
	}
	
}
