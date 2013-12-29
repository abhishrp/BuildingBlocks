package com.nextwaretech.buildingblockscre;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MainTabFragment extends Fragment {
	private static View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.main_tab_fragment, container, false);
		final Button CONTACTS = (Button) view.findViewById(R.id.contacts_tab);
		final Button PROPERTIES = (Button) view.findViewById(R.id.properties_tab);
		final Button LISTINGS = (Button) view.findViewById(R.id.listing_tab);
		final Button INCOMING_CALLS = (Button) view
				.findViewById(R.id.incoming_call_tab);
		CONTACTS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CONTACTS.setClickable(false);
				PROPERTIES.setClickable(true);
				LISTINGS.setClickable(true);
				INCOMING_CALLS.setClickable(true);
				
				Intent intent = new Intent(
						"com.nextwaretech.buildingblockscre.AllContactsActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				
			}
		});

		
		PROPERTIES.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CONTACTS.setClickable(true);
				PROPERTIES.setClickable(false);
				LISTINGS.setClickable(true);
				INCOMING_CALLS.setClickable(true);
				Intent intent = new Intent(
						"com.nextwaretech.buildingblockscre.AllPropertiesActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});

		
		LISTINGS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CONTACTS.setClickable(true);
				PROPERTIES.setClickable(true);
				LISTINGS.setClickable(false);
				INCOMING_CALLS.setClickable(true);
				Intent intent = new Intent(
						"com.nextwaretech.buildingblockscre.AllListingsActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});

		
		INCOMING_CALLS.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CONTACTS.setClickable(true);
				PROPERTIES.setClickable(true);
				LISTINGS.setClickable(true);
				INCOMING_CALLS.setClickable(false);
				Intent intent = new Intent(
						"com.nextwaretech.buildingblockscre.IncomingCallActivity");
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});

		return view;
	}
}
