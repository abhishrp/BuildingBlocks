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

public class PropertiesArrayAdapter extends ArrayAdapter<JSONObject> {
	private final Activity activity;
	private final Context context;
	private final LinkedList<JSONObject> properties;
	private final boolean checkmark;

	public PropertiesArrayAdapter(Activity activity, Context context,
			LinkedList<JSONObject> propertiesList, boolean checkmark) {
		super(context, R.layout.list_row, propertiesList);
		this.activity = activity;
		this.context = context;
		this.properties = propertiesList;
		this.checkmark = checkmark;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int textSizeinDP = 15;
		int textSize = SizeTransform.dpToPixel(context, textSizeinDP);
		
		LayoutParams params;
		
		int addressWidth = activity.findViewById(R.id.table_col1)
				.getWidth();
		int propNameWidth = activity.findViewById(R.id.table_col2).getWidth();
		int typeWidth = activity.findViewById(R.id.table_col3)
				.getWidth();
		int sizeWidth = activity.findViewById(R.id.table_col4).getWidth();
		int categoryWidth = activity.findViewById(R.id.table_col5).getWidth();

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
		TextView address = (TextView) rowView.findViewById(R.id.col1);
		TextView propName = (TextView) rowView.findViewById(R.id.col2);
		TextView type = (TextView) rowView.findViewById(R.id.col3);
		TextView size = (TextView) rowView.findViewById(R.id.col4);
		TextView category = (TextView) rowView.findViewById(R.id.col5);

		try {
			String addr = properties.get(position).getString("site_address"); 
			if(addr!=null)
				address.setText(addr);
			else
				address.setText("-");
			address.setTextSize(textSize);
			params = (LayoutParams) address.getLayoutParams();
			params.width = addressWidth;
			address.setLayoutParams(params);
			
			String pname = properties.get(position).getString("name"); 
			if(pname!=null && !pname.equals("null"))
				if(pname.length()!=0)
					propName.setText(pname);
				else
					propName.setText("-");
			else
				propName.setText("-");
			propName.setTextSize(textSize);
			params = (LayoutParams) propName.getLayoutParams();
			params.width = propNameWidth;
			propName.setLayoutParams(params);
			
			if(properties.get(position).getJSONObject("property_type").getString("virtual_property_type")!=null)
				type.setText(properties.get(position).getJSONObject("property_type").getString("virtual_property_type"));
			else
				type.setText("-");
			type.setTextSize(textSize);
			params = (LayoutParams) type.getLayoutParams();
			params.width = typeWidth;
			type.setLayoutParams(params);
			
			if(properties.get(position).getString("building_size")!=null)
				size.setText(properties.get(position).getString("building_size"));
			else
				size.setText("-");
			size.setTextSize(textSize);
			params = (LayoutParams) size.getLayoutParams();
			params.width = sizeWidth;
			size.setLayoutParams(params);
			
			//commented because category field is not present in the property JSON
			//What should be the last column? It shows share on the website, but 
			//this column was not used in the contacts page.
			//if(properties.get(position).getString("category")!=null)
				//category.setText(properties.get(position).getString("category"));
			//else
			category.setText("-");
			category.setTextSize(textSize);
			params = (LayoutParams) category.getLayoutParams();
			params.width = categoryWidth;
			category.setLayoutParams(params);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return rowView;
	}
}