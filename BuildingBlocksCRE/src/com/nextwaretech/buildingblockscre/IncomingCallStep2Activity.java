package com.nextwaretech.buildingblockscre;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TabHost;

import com.nextwaretech.buildingblockscre.R;

public class IncomingCallStep2Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incoming_call_step2);

		Button step2ContinueButton = (Button) findViewById(R.id.in_call_step2_continue);
		step2ContinueButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
				int index = radioGroup.indexOfChild(findViewById(radioGroup
						.getCheckedRadioButtonId()));
				switch (index) {
				case 0: {
					IncomingCallActivity.inquiry_class = "Listing";
					break;
				}

				case 1: {
					IncomingCallActivity.inquiry_class = "Contact";
					break;
				}

				case 2: {
					IncomingCallActivity.inquiry_class = "Property";
					break;
				}

				}
				Activity tabs = getParent();
				((TabHost) tabs.findViewById(android.R.id.tabhost))
						.setCurrentTab(2);
			}
		});
	}
}
