package com.nextwaretech.buildingblockscre;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartUpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_up);
		
		Thread timer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);	//milliseconds
					//New Activity could start here
				} catch(InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent intent = new Intent("com.nextwaretech.buildingblockscre.SignInActivity");
					startActivity(intent);
				}
			}
		};
		timer.start();
	}
	
	@Override
	protected void onRestart() {
		finish();
		super.onRestart();
	}
}
