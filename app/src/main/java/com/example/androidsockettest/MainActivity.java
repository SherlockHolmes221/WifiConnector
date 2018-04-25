package com.example.androidsockettest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static ServerSocket serverSocket = null;
	public  TextView textView1;
	private String IP = "";
	String buffer = "";

	private static final String WAKE = "0";
	private static final String RING = "1";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.activity_main);
		textView1 = (TextView) findViewById(R.id.textView1);
		IP = getlocalip();
		textView1.setText("IP addresss:"+IP);

		new Thread() {
			public void run() {
				
				OutputStream output;
				String str = "hello hehe";
				try {
					serverSocket = new ServerSocket(30000);
					while (true) {
						try {
							Socket socket = serverSocket.accept();
							output = socket.getOutputStream();
							output.write(str.getBytes("gbk"));
							output.flush();
							socket.shutdownOutput();
						
							BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String line = null;
							buffer = "";
							while ((line = bff.readLine())!=null) {
								buffer = line + buffer;
							}
							
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showToast(buffer);
									react(buffer);
								}
							});
							
							bff.close();
							output.close();
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			};
		}.start();
	}

	private void react(String buffer) {
		if(buffer.equals(WAKE)){
			Log.e("react","wake");
			AppUtils.wakeUpAndUnlock();

		}else if(buffer.equals(RING)){
			Log.e("react","ring");
			AppUtils.playSound(this);

		} else{
			AppUtils.wakeUpAndUnlock();
			Log.e("react","show");
			Intent intent = new Intent();
			intent.setClass(this,MessageDialog.class);
			intent.putExtra("color",buffer);
			startActivity(intent);
		}
}

	private void showToast(String s) {
		Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
	}
	
	private String getlocalip(){
		WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		//  Log.d(Tag, "int ip "+ipAddress);
		if(ipAddress==0)return null;
		return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
				+(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		try {
//			serverSocket.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}