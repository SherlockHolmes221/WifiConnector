package com.example.client;

import java.io.BufferedReader;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	Socket socket = null;
	String buffer = "";
	TextView txt1;
	TextView txt2;
	Button send;
	EditText ed1;
	String geted1;
	String ip;
	MyThread myThread;

	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x11) {
				Bundle bundle = msg.getData();
				txt1.append("反馈信息:"+bundle.getString("msg")+"\n");
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		txt1 = (TextView) findViewById(R.id.txt1);
		txt2 = (TextView) findViewById(R.id.ip);
		send = (Button) findViewById(R.id.send);
		ed1 = (EditText) findViewById(R.id.ed1);


		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				geted1 = ed1.getText().toString();
				txt1.append("发送信息:"+geted1+"\n");
				//启动线程 向服务器发送和接收信息
				new MyThread(geted1).start();
			}
		});
		ip = getIpAddressString();
		txt2.setText("IP addresss:"+ip);
	}

	class MyThread extends Thread {

		public ServerSocket serverSocket = null;

		public String string;

		public MyThread(String s) {
			string = s;
		}

		//写入信息并获取反馈信息
		@Override
		public void run() {

			try {
				Log.e("try","try");
				//连接服务器 并设置连接超时为5秒
				socket = new Socket();
				socket.connect(new InetSocketAddress("192.168.43.15", 30000), 1000);

				//定义消息
				Message msg = new Message();
				msg.what = 0x11;
				Bundle bundle = new Bundle();
				bundle.clear();

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

					bundle.putString("msg", buffer.toString());
					msg.setData(bundle);
					//发送消息 修改UI线程中的组件
					myHandler.sendMessage(msg);
					//关闭各种输入输出流
				   bff.close();
					outputStream.close();
					Log.e("try1","try1");
				}catch (IOException e) {
					//连接超时 在UI界面显示消息
					bundle.putString("msg", "服务器连接失败！请检查网络是否打开");
					msg.setData(bundle);
					//发送消息 修改UI线程中的组件
					myHandler.sendMessage(msg);
					e.printStackTrace();
				}

			} catch (SocketTimeoutException aa) {
				aa.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	public static String getIpAddressString() {
		try {
			for (Enumeration<NetworkInterface> enNetI = NetworkInterface
					.getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
				NetworkInterface netI = enNetI.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = netI
						.getInetAddresses();enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					Log.e("ip",inetAddress.toString());
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address
							&& !inetAddress.isLoopbackAddress() && inetAddress.toString().substring(0,3).equals("/19")) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "";
	}
}
;