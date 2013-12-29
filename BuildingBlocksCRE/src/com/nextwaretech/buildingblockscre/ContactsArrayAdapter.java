package com.nextwaretech.buildingblockscre;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
		Resources resource = context.getResources();
		float textSize = resource.getDimension(R.dimen.large_text_size);
		
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
			String firstStr = contacts.get(position).getString("first_name").trim();
			if(firstStr!=null){
					firstName.setText(firstStr);
			}
			else{
				firstName.setText("-");
			}
			firstName.setTextSize(textSize);
			params = (LayoutParams) firstName.getLayoutParams();
			params.width = firstNameWidth;
			params.bottomMargin = (int)context.getResources().getDimension(R.dimen.padding_small);
			params.topMargin = (int)context.getResources().getDimension(R.dimen.padding_small);
			firstName.setLayoutParams(params);
			
			String lastStr = contacts.get(position).getString("last_name").trim();
			if(lastStr!=null){
					lastName.setText(lastStr);
			}
			else{
				lastName.setText("-");
			}
			lastName.setTextSize(textSize);
			params = (LayoutParams) lastName.getLayoutParams();
			params.width = lastNameWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			lastName.setLayoutParams(params);
			
			String compStr = contacts.get(position).getString("company_name").trim();
			if(compStr!=null){
					compName.setText(compStr);
			}
			else{
				compName.setText("-");
			}
			compName.setTextSize(textSize);
			params = (LayoutParams) compName.getLayoutParams();
			params.width = companyNameWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			compName.setLayoutParams(params);
			
			String catStr = contacts.get(position).getString("category").trim();
			if(catStr!=null){
					category.setText(catStr);
			}
			else{
				category.setText("-");
			}
			category.setTextSize(textSize);
			params = (LayoutParams) category.getLayoutParams();
			params.width = categoryWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			category.setLayoutParams(params);
			
			String phoneStr = contacts.get(position).getString("phone_no").trim();
			if(phoneStr!=null){
					phone.setText(phoneStr);
			}
			else{
				phone.setText("-");
			}
			phone.setTextSize(textSize);
			params = (LayoutParams) phone.getLayoutParams();
			params.width = phoneNoWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			phone.setLayoutParams(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return rowView;
	}
}
