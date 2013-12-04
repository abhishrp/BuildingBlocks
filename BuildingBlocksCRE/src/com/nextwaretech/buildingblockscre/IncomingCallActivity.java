package com.nextwaretech.buildingblockscre;

import org.json.JSONObject;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.nextwaretech.buildingblockscre.R;

public class IncomingCallActivity extends TabActivity {

	private static final String URI_NO_AUTH_TOKEN = Data.SERVER_NAME+"incoming_calls.json?auth_token=";
	private static StringBuilder uriAuthTokenAdded;
	public static int incomingCallId = -1;
	public static String inquiry_class;
	public static JSONObject incoming_call_data = new JSONObject();
	private TabHost tabHost;
	private TabSpec specs;
	private TabWidget tabWidget;
	private View tabIndicator;
	private TextView stepNo;
	private TextView tabName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incoming_call);
		Button incoming_call_tab = (Button) findViewById(R.id.incoming_call_tab);
		incoming_call_tab.setBackgroundColor(getResources().getColor(R.color.bg));
		
		uriAuthTokenAdded = new StringBuilder(URI_NO_AUTH_TOKEN);
		uriAuthTokenAdded.append(Data.authToken);
		
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		//tabHost = getTabHost();
		//tabHost.setup();
		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		setupTabs();
		tabWidget.getChildTabViewAt(1).setEnabled(false);
		tabWidget.getChildTabViewAt(2).setEnabled(false);
		
		Button contactsTab = (Button) findViewById(R.id.contacts_tab);
		contactsTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.nextwaretech.buildingblockscre.AllContactsActivity");
				startActivity(intent);
			}
		});
		
	}
	
	private void setupTabs() {
		addTab("Step 1", "Enter Call Information", IncomingCallStep1Activity.class);
		addTab("Step 2", "Choose Type Of Link", IncomingCallStep2Activity.class);
		addTab("Step 3", "Lookup & Link", IncomingCallStep3Activity.class);
	}
	
	private void addTab(String specname, String tabname, Class<?> c) {
		tabIndicator = getLayoutInflater().inflate(R.layout.tab_indicator, null);
		specs = tabHost.newTabSpec(specname);
	    stepNo = (TextView) tabIndicator.findViewById(R.id.step_no);
	    stepNo.setText(specname);
		tabName = (TextView) tabIndicator.findViewById(R.id.tab_name);
	    //stepNo.setWidth(tab_name.getWidth());
	    tabName.setText(tabname);
	    specs.setIndicator(tabIndicator);
	    Intent intent = new Intent(this, c);
	    specs.setContent(intent);
	    tabHost.addTab(specs);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building_block_cre, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.logout:
			LogOutTask logout = new LogOutTask(this);
			logout.execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
