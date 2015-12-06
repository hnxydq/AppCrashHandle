package com.dq.appcrashhandle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dq.appcrashhandle.service.CrashReportService;
import com.dq.appcrashhandle.util.Constants;
import com.dq.appcrashhandle.util.HandlerManager;

public class MainActivity extends Activity implements OnClickListener{
	private static final String TAG = "MainActivity";
	
	private Button btn;
	
	private Handler uiHandler = new Handler(){
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case Constants.APP_CRASH_TIPS:
				Log.d(TAG, "received .....");
				MainActivity.this.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						//showCrashDialog(MainActivity.this, (String)msg.obj);
						
						Toast.makeText(MainActivity.this, (String)msg.obj, 0).show();
					}
					
				});
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		HandlerManager.setHandler(uiHandler);
		initView();
		Intent intent = new Intent(this, CrashReportService.class);
		startService(intent);
	}

	private void initView() {
		btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == btn) {
			throw new RuntimeException("自定义的异常！");
		}
	}
	
	//因为这边收不到handler的消息，所以这个办法没法实现；除非，重新startActivity
	/*private void showCrashDialog(Context context, String msg) {
		AlertDialog.Builder builder = new Builder(context);
		View view = View.inflate(context, R.layout.dialog, null);
		TextView tv = (TextView) view.findViewById(R.id.text);
		Button btn = (Button) view.findViewById(R.id.btn);
		tv.setText(msg);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Process.killProcess(Process.myPid());
				System.exit(0);
			}
		});
		builder.setView(view);
		AlertDialog dialog = builder.create();
		dialog.show();
	}*/

}
