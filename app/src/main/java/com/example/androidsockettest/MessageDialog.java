package com.example.androidsockettest;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by quxia on 2018/4/25.
 */

public class MessageDialog extends Activity {

    private LinearLayout linearLayout;
    private static final String DIALOG_WHITE = "2";
    private static final String DIALOG_RED = "3";
    private static final String DIALOG_BLUE = "4";
    private static final String DIALOG_YELLOW = "5";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_dialog);
        linearLayout = findViewById(R.id.dialog_layout);

        Intent intent = getIntent();
        String s = intent.getStringExtra("color");

        if(s.equals(DIALOG_WHITE)){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }else if(s.equals(DIALOG_RED)){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.red));
        }else if(s.equals(DIALOG_BLUE)){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }else if(s.equals(DIALOG_YELLOW)){
            linearLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
        }
    }
}
