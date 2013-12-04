package com.nextwaretech.buildingblockscre;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.nextwaretech.buildingblockscre.R;

public class ContactsArrayAdapter extends ArrayAdapter<JSONObject> {
	private final Activity activity;
	private final Context context;
	private final LinkedList<JSONObject> contacts;
	private final boolean checkmark;

	public ContactsArrayAdapter(Activity activity, Context context,
			LinkedList<JSONObject> contactsList, boolean checkmark) {
		super(context, R.layout.list_row, contactsList);
		this.activity = activity;
		this.context = context;
		this.contacts = contactsList;
		this.checkmark = checkmark;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int textSizeinDP = 15;
		int textSize = SizeTransform.dpToPixel(context, textSizeinDP);
		
		LayoutParams params;
		
		int firstNameWidth = activity.findViewById(R.id.table_col1)
				.getWidth();
		int lastNameWidth = activity.findViewById(R.id.table_col2).getWidth();
		int companyNameWidth = activity.findViewById(R.id.table_col3)
				.getWidth();
		int categoryWidth = activity.findViewById(R.id.table_col4).getWidth();
		int phoneNoWidth = activity.findViewById(R.id.table_col5).getWidth();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_row, parent, false);
		if(!checkmark) {
			//int check_mark_width = activity.findViewById(R.id.select).getWidth();
			ImageView check = (ImageView) rowView.findViewById(R.id.check_mark);
			/*
			params = (LayoutParams) check.getLayoutParams();
			params.width = check_mark_width;
			check.setLayoutParams(params);*/
			check.setVisibility(View.GONE);
		}
		TextView firstName = (TextView) rowView.findViewById(R.id.col1);
		TextView lastName = (TextView) rowView.findViewById(R.id.col2);
		TextView compName = (TextView) rowView.findViewById(R.id.col3);
		TextView category = (TextView) rowView.findViewById(R.id.col4);
		TextView phone = (TextView) rowView.findViewById(R.id.col5);

		try {
			if(contacts.get(position).getString("first_name")!=null)
				firstName.setText(contacts.get(position).getString("first_name"));
			else
				firstName.setText("-");
			firstName.setTextSize(textSize);
			params = (LayoutParams) firstName.getLayoutParams();
			params.width = firstNameWidth;
			firstName.setLayoutParams(params);
			
			if(contacts.get(position).getString("last_name")!=null)
				lastName.setText(contacts.get(position).getString("last_name"));
			else
				lastName.setText("-");
			lastName.setTextSize(textSize);
			params = (LayoutParams) lastName.getLayoutParams();
			params.width = lastNameWidth;
			lastName.setLayoutParams(params);
			
			if(contacts.get(position).getString("company_name")!=null)
				compName.setText(contacts.get(position).getString("company_name"));
			else
				compName.setText("-");
			compName.setTextSize(textSize);
			params = (LayoutParams) compName.getLayoutParams();
			params.width = companyNameWidth;
			compName.setLayoutParams(params);
			
			if(contacts.get(position).getString("category")!=null)
				category.setText(contacts.get(position).getString("category"));
			else
				category.setText("-");
			category.setTextSize(textSize);
			params = (LayoutParams) category.getLayoutParams();
			params.width = categoryWidth;
			category.setLayoutParams(params);
			
			if(contacts.get(position).getString("phone_no")!=null)
				phone.setText(contacts.get(position).getString("phone_no"));
			else
				phone.setText("-");
			phone.setTextSize(textSize);
			params = (LayoutParams) phone.getLayoutParams();
			params.width = phoneNoWidth;
			phone.setLayoutParams(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return rowView;
	}
}
