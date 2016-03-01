package com.dq.appcrashhandle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.dq.appcrashhandle.service.CrashReportService;

public class CrashHandler implements UncaughtExceptionHandler {

	private static final String TAG = "CrashHandler";
	private static final boolean DEBUG = true;
	
	private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/crash/log/";
	private static final String FILE_NAME = "crash";
	private static final String FILE_NAME_SUFFIX = ".trace";
	
	private static volatile CrashHandler sInstance;
	private UncaughtExceptionHandler mDefaultHandler;
	private Context mContext;
	
	private CrashHandler() {
		//forbid new operation
	}
	
	public static CrashHandler getInstance() {
		if(sInstance == null) {
			synchronized(CrashHandler.class) {
				if(sInstance == null) {
					sInstance = new CrashHandler();
				}
			}
		}
		return sInstance;
	}
	
	public void init(Context context) {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		mContext = context.getApplicationContext();
	}
	
	@Override
	public void uncaughtException(Thread thread, final Throwable ex) {
		
		if(!handleException(ex) && mDefaultHandler != null) {
			//自定义异常处理器未处理，则让系统默认的异常处理器来处理  
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			//等待3秒钟退出
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Process.killProcess(Process.myPid());
			System.exit(1);
		}
	}

	//自行处理未处理异常
	private boolean handleException(Throwable ex) {
		if(ex == null) {
			return false;
		}
		
		//1.使用handler不行，应该是应用死掉了，所以MainActivity也不能接收到消息
		/*Handler uiHandler = HandlerManager.getHandler();
		Message msg = Message.obtain();
		msg.what = Constants.APP_CRASH_TIPS;
		msg.obj = ex.toString();
		uiHandler.sendMessage(msg);*/
		
        String fileName = saveExceptionToSdcard(ex);
        if(!TextUtils.isEmpty(fileName)) {
        	uploadExceptionToServer(fileName);//如果适用只弹出一个toast提示的方式，保存到本地和提交服务器都可以在这个文件做
        									  //如果想提示用户手动去提交，可以放到service中或者直接start一个新的Activity(其实，
        									  //service最终也得转化为startActivity去做，也就是方法4,5).
        }
        
        //2.使用Toast来显示异常信息 ---可行  
        /*new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(mContext, "很抱歉,应用出现异常,即将退出.", Toast.LENGTH_LONG).show();  
                Looper.loop();  
            }  
        }.start();*/
        
        //3.广播的方式不行-----原因应该是应用死掉了，广播接收者无法收到广播
        /*Intent intent = new Intent("com.crash.test");
        intent.putExtra("crash_info", ex.toString());
        mContext.sendBroadcast(intent);*/
        
        //4.可以直接打开一个Activity来操作
        /*Intent intent = new Intent(mContext, CrashReportActivity.class);
        intent.putExtra("crash_info", ex.toString());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);*/
        
        //5.通过服务的方式，其实与4差不多
        CrashReportService.reportCrash(fileName);
		return true;
	}

	private void uploadExceptionToServer(String fileName) {
		//TODO:
		Log.d(TAG, "file to be upload: " + fileName);
	}

	private String saveExceptionToSdcard(Throwable ex) {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if(DEBUG) {
				Log.w(TAG, "sdcard unmounted, skip save exception");
				return null;
			}
		}
		//创建路径
		File dir = new File(PATH);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		Log.d(TAG, "dir exists: " + dir.exists());
		long current = System.currentTimeMillis();
		String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
		//创建文件
		String sTime = time.replace(' ', '-').replace(':', '-');
		File file = new File(PATH + FILE_NAME + "-" + sTime + FILE_NAME_SUFFIX);
		//Log.d(TAG, "file path: " + file.getAbsolutePath());
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			pw.println(time);
			savePhoneInfo(pw);
			pw.println();
			ex.printStackTrace(pw);
			pw.close();
			return file.getName();
		} catch (Exception e) {
			Log.e(TAG, "save crash info failed.\n" + e.toString());
		} 
		return null;
	}
	
	/**
	 * 导出手机信息
	 * @param pw
	 * @throws NameNotFoundException 
	 */
	private void savePhoneInfo(PrintWriter pw) throws NameNotFoundException {
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
		pw.print("App Version: ");
		pw.print(pi.versionName);
		pw.print('_');
		pw.println(pi.versionCode);
		
		//android的版本号
		pw.print("OS Version: ");
		pw.print(Build.VERSION.RELEASE);
		pw.print('_');
		pw.println(Build.VERSION.SDK_INT);
		
		//手机制造商
		pw.print("Vendor: ");
		pw.print(Build.MANUFACTURER);
		
		//手机型号
		pw.print("Model: ");
		pw.println(Build.MODEL);
		
		//CPU架构
		pw.print("CPU ABI: ");
		pw.println(Build.CPU_ABI);
		
	}

}
