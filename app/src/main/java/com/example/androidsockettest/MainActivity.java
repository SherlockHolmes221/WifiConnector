package com.example.androidsockettest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static ServerSocket serverSocket = null;
	public  TextView textView1;
	private String IP = "";
	String buffer = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
						Message msg = new Message();
						msg.what = 0x11;
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
		if(buffer.equals("0")){
			Log.e("react","wake");

		}else if(buffer.equals("1")){
			Log.e("react","show");

		}else if(buffer.equals("2")){
			Log.e("react","ring");

		}
}

	private void showToast(String s) {
		Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
	}
	
	private String getlocalip(){
		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		//  Log.d(Tag, "int ip "+ipAddress);
		if(ipAddress==0)return null;
		return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
				+(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
	}
}