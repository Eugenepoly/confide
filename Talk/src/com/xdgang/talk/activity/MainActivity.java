package com.xdgang.talk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.view.RenrenAuthListener;
import com.xdgang.talk.R;

public class MainActivity extends BaseActivity {

	protected static final String TAG = "MainActivity";
	
	private static final String API_KEY = "6b1016db20c540e78bd1b20be4c707a3";

	private static final String SECRET_KEY = "4723a695c09e4ddebbe8d87393d95fb4";

	private static final String APP_ID = "105381";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
		RelativeLayout mainLayout = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.activity_main, null);
		root.addView(mainLayout);
		renren = new Renren(API_KEY, SECRET_KEY, APP_ID, this);
		initViews();
	}

	private void initViews() {
		titleText.setText(R.string.app_name);
		findViewById(R.id.login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				renren.authorize((Activity) mContext, new RenrenAuthListener() {

					@Override
					public void onRenrenAuthError(
							RenrenAuthError renrenAuthError) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onComplete(Bundle values) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext,
								FriendListActivity.class);
						intent.putExtra(Renren.RENREN_LABEL, renren);
						startActivity(intent);
					}

					@Override
					public void onCancelLogin() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onCancelAuth(Bundle values) {
						// TODO Auto-generated method stub

					}
				});
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
