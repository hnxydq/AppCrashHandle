package com.dq.appcrashhandle.util;

import android.os.Handler;

public class HandlerManager {

	private static ThreadLocal<Handler> sThreadLocal = new ThreadLocal<Handler>();
	
	public static void setHandler(Handler handler) {
		sThreadLocal.set(handler);
	}
	
	public static Handler getHandler() {
		return sThreadLocal.get();
	}
	
}
