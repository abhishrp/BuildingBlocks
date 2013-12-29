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

public class ListingsArrayAdapter extends ArrayAdapter<JSONObject> {
	private final Activity activity;
	private final Context context;
	private final LinkedList<JSONObject> listings;
	private final boolean checkmark;

	public ListingsArrayAdapter(Activity activity, Context context,
			LinkedList<JSONObject> listingsList, boolean checkmark) {
		super(context, R.layout.list_row, listingsList);
		this.activity = activity;
		this.context = context;
		this.listings = listingsList;
		this.checkmark = checkmark;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Resources resource = context.getResources();
		float textSize = resource.getDimension(R.dimen.large_text_size);
		
		LayoutParams params;
		
		int addressWidth = activity.findViewById(R.id.table_col1)
				.getWidth();
		int typeWidth = activity.findViewById(R.id.table_col2).getWidth();
		int propTypeWidth = activity.findViewById(R.id.table_col3)
				.getWidth();
		int teamWidth = activity.findViewById(R.id.table_col4).getWidth();
		int statusWidth = activity.findViewById(R.id.table_col5).getWidth();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_row, parent, false);
		if(!checkmark) {
			ImageView check = (ImageView) rowView.findViewById(R.id.check_mark);
			check.setVisibility(View.GONE);
		}
		TextView address = (TextView) rowView.findViewById(R.id.col1);
		TextView type = (TextView) rowView.findViewById(R.id.col2);
		TextView propType = (TextView) rowView.findViewById(R.id.col3);
		TextView team = (TextView) rowView.findViewById(R.id.col4);
		TextView status = (TextView) rowView.findViewById(R.id.col5);

		try {
			JSONObject property = listings.get(position).getJSONObject("property");
			if(property!=null){
				String addr = property.getString("site_address");
				if(addr!=null)
					address.setText(addr);
				else
					address.setText("-");
			}
			address.setTextSize(textSize);
			params = (LayoutParams) address.getLayoutParams();
			params.width = addressWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			address.setLayoutParams(params);
			
			
			JSONObject listingDetails = listings.get(position).getJSONObject("listing_details");
			if(listingDetails!=null){
				String ty = listingDetails.getString("listing_type"); 
				if(ty!=null)
					type.setText(ty);
				else
					type.setText("-");
			}
			
			type.setTextSize(textSize);
			params = (LayoutParams) type.getLayoutParams();
			params.width = typeWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			type.setLayoutParams(params);
			
			if(property!=null){
				JSONObject propTy = property.getJSONObject("property_type");
				if(propTy!=null){
					String prTy = propTy.getString("virtual_property_type");
					if(prTy!=null)
						propType.setText(prTy);
					else
						propType.setText("-");
				}
			}
			propType.setTextSize(textSize);
			params = (LayoutParams) propType.getLayoutParams();
			params.width = propTypeWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			propType.setLayoutParams(params);
			
			String tm = listings.get(position).getString("team_name"); 
			if(tm!=null)
				team.setText(tm);
			else
				team.setText("-");
			team.setTextSize(textSize);
			params = (LayoutParams) team.getLayoutParams();
			params.width = teamWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			team.setLayoutParams(params);
			
			JSONObject listingStatus = listings.get(position).getJSONObject("listing_status");
			if(listingStatus!=null){
				String st = listingStatus.getString("name");
				if(st!=null)
					status.setText(st);
				else
					status.setText("-");
			}
			status.setTextSize(textSize);
			params = (LayoutParams) status.getLayoutParams();
			params.width = statusWidth;
			params.bottomMargin = (int)resource.getDimension(R.dimen.padding_small);
			params.topMargin = (int)resource.getDimension(R.dimen.padding_small);
			status.setLayoutParams(params);
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return rowView;
	}
}
