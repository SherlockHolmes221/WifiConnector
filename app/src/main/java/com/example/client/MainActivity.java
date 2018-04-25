package com.example.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String WAKE = "0";
	private static final String RING = "1";
	private static final String DIALOG_WHITE = "2";
	private static final String DIALOG_RED = "3";
	private static final String DIALOG_BLUE = "4";
	private static final String DIALOG_YELLOW = "5";

	Socket socket = null;
	String buffer = "";

	int position = 0;

	Button wake;
	Button ring;
	Button show;
	Spinner spinner;

	ArrayList<String> list = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wake = (Button) findViewById(R.id.btn_wake);//0
		ring = (Button) findViewById(R.id.btn_ring);//1
		show = (Button) findViewById(R.id.btn_show);
		spinner = findViewById(R.id.spinner_color);

		wake.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//启动线程 向服务器发送和接收信息
				new MyThread(WAKE).start();
			}
		});

		ring.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//启动线程 向服务器发送和接收信息
				new MyThread(RING).start();
			}
		});

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				position = i;
				Log.e("position", String.valueOf(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//启动线程 向服务器发送和接收信息
				if(position == 0){
					new MyThread(DIALOG_WHITE).start();
				}else if(position == 1){
					new MyThread(DIALOG_RED).start();
				}else if(position == 2){
					new MyThread(DIALOG_BLUE).start();
				}else if(position == 3){
					new MyThread(DIALOG_YELLOW).start();
				}
			}
		});
	}

	class MyThread extends Thread {

		public String string;

		public MyThread(String s) {
			string = s;
		}
		//写入信息并获取反馈信息
		@Override
		public void run() {
			list = getConnectedIP();
			for(final String ip:list){
				Log.e("ip",ip);

				try {
					Log.e("try","try");
					//连接服务器 并设置连接超时为5秒
					socket = new Socket();
					socket.connect(new InetSocketAddress(ip, 30000), 1000);

					BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					//读取发来服务器信息
					String line = null;
					buffer="";
					try {
						while ((line = bff.readLine()) != null) {
							buffer = line + buffer;
						}
						OutputStream outputStream = socket.getOutputStream();
						outputStream.write(string.getBytes("gbk"));
						outputStream.flush();

						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showToast("成功向"+ip+"传输");
							}
						});
						//关闭各种输入输出流
						bff.close();
						outputStream.close();
					}catch (IOException e) {
						//连接超时 在UI界面显示消息
						e.printStackTrace();
					}
				} catch (SocketTimeoutException aa) {
					aa.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	private void showToast(String s) {
		Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private ArrayList<String> getConnectedIP() {
		ArrayList<String> connectedIP = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"/proc/net/arp"));
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitted = line.split(" +");
				if (splitted != null && splitted.length >= 4) {
					String ip = splitted[0];
					if(!ip.equals("IP")){
						connectedIP.add(ip);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connectedIP;
	}
}