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
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static ServerSocket serverSocket = null;
	public static TextView mTextView, textView1;
    private String IP = "";
    String buffer = "";
	public static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what==0x11) {
				Bundle bundle = msg.getData();
				mTextView.append("client"+bundle.getString("msg")+"\n");
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.textsss);
		textView1 = (TextView) findViewById(R.id.textView1);
		IP = getlocalip();
		textView1.setText("IP addresss:"+IP);
		new Thread() {
			public void run() {
				Bundle bundle = new Bundle();
				bundle.clear();
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
							//mHandler.sendEmptyMessage(0);
							BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String line = null;
							buffer = "";
							while ((line = bff.readLine())!=null) {
								buffer = line + buffer;
							}
							bundle.putString("msg", buffer.toString());
							msg.setData(bundle);
							mHandler.sendMessage(msg);
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
