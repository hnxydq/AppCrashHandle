package com.dq.appcrashhandle;

import java.io.File;

import com.dq.appcrashhandle.util.NetworkManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CrashReportActivity extends Activity implements OnClickListener{

	private static final String TAG = "CrashReportActivity";
	private TextView msg;
	private Button confirm, cancel;
	private String info = "";
	private String filePath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crash_report_layout);
		
		Intent intent = getIntent();
		info = intent.getStringExtra("crash_info");
		filePath = intent.getStringExtra("filePath");
		msg = (TextView) findViewById(R.id.msg);
		msg.setText(info);
		
		confirm = (Button) findViewById(R.id.confirm);
		cancel = (Button) findViewById(R.id.cancel);
		
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		if(v == cancel) {
			this.finish();
		} else if(v == confirm) {
			Log.d(TAG, "main:" + info);
			boolean b = reportCrashToServer(info);
			//上传成功，把保存的文件删除
			Log.d(TAG, "filePath: " + filePath);
			if(b) {
				//delete : 还需要处理用户选择不上传的文件，后续再优化
				File file = new File(filePath);
				if(file.exists()) {
					file.delete();
				}
			}
			
			this.finish();
		}
	}
	
	
	private boolean reportCrashToServer(String info) {
		//根据上传成功与失败返回
		//判断网络是否连接
		if(NetworkManager.isNetworkAvailable(this)) {
			//TODO:
			return true;
		}
		return false;
	}
}
