package com.xdgang.talk.activity;

import com.renren.api.connect.android.Renren;
import com.xdgang.talk.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	/**
	 * 根布局对象
	 */
	protected LinearLayout root;

	/**
	 * 标题栏左边按钮
	 */
	protected Button leftButton;

	/**
	 * 标题栏中间文字
	 */
	protected TextView titleText;

	/**
	 * 标题栏右边按钮
	 */
	protected ImageButton rightButton;
	/**
	 * 调用SDK接口的Renren对象
	 */
	protected Renren renren;
	
	protected Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.base_layout);
		root = (LinearLayout) findViewById(R.id.lin_layout);
		leftButton = (Button) findViewById(R.id.title_left_btn);
		titleText =  (TextView) findViewById(R.id.title_center_text);
		rightButton =  (ImageButton) findViewById(R.id.title_right_btn);
		
		Intent intent = getIntent();
		renren = intent.getParcelableExtra(Renren.RENREN_LABEL);
		if (renren != null) {
			renren.init(this);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	void showToast(String text, boolean isLong) {
		// TODO 显示Toast
		int duration = Toast.LENGTH_SHORT;
		if (isLong) {
			duration = Toast.LENGTH_LONG;
		}
		Toast.makeText(mContext, text, duration).show();
	}

}
