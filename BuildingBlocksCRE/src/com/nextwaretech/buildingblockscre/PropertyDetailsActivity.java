package com.nextwaretech.buildingblockscre;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nextwaretech.buildingblockscre.R;

public class PropertyDetailsActivity extends Activity {

	private static StringBuilder uriAuthTokenAdded;
	private static final String URI_INCOMPLETE = Data.SERVER_NAME+"properties/";
	private static int propertyId;
	private ImageView propertyImage;
	private TextView address1, address2, propertyType, subType, propertySize,
			buildingClass, noOfUnits, zoning, parking, lotSize, buildingUse,
			construction, parcelNo, parking1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.property_details);

		TextView headerName = (TextView) findViewById(R.id.header_name);
		headerName.setText("Properties");
		
		Button properties_tab = (Button) findViewById(R.id.properties_tab);
		properties_tab.setBackgroundColor(getResources().getColor(R.color.bg));

		uriAuthTokenAdded = new StringBuilder(URI_INCOMPLETE);

		propertyImage = (ImageView) findViewById(R.id.property_image);
		address1 = (TextView) findViewById(R.id.address1);
		address2 = (TextView) findViewById(R.id.address2);
		propertyType = (TextView) findViewById(R.id.property_type_value);
		subType = (TextView) findViewById(R.id.sub_type_value);
		propertySize = (TextView) findViewById(R.id.property_size_value);
		buildingClass = (TextView) findViewById(R.id.building_class_value);
		noOfUnits = (TextView) findViewById(R.id.no_of_units_value);
		zoning = (TextView) findViewById(R.id.zoning_value);
		parking = (TextView) findViewById(R.id.parking_value);
		lotSize = (TextView) findViewById(R.id.lot_size_value);
		buildingUse = (TextView) findViewById(R.id.building_use_value);
		construction = (TextView) findViewById(R.id.construction_value);
		parcelNo = (TextView) findViewById(R.id.parcel_no_value);
		parking1000 = (TextView) findViewById(R.id.parking_1000_value);

		Bundle extras = getIntent().getExtras();
		String str = "";
		if (extras != null) {
			str = extras.getString("propertyJSONString");
		}
		
		try {
			JSONObject property = new JSONObject(str);
			propertyId = property.getInt("id");
			uriAuthTokenAdded.append("/");
			uriAuthTokenAdded.append(propertyId);
			uriAuthTokenAdded.append("/new_note.json?auth_token=");
			uriAuthTokenAdded.append(Data.authToken);

			address1.setText(property.getString("site_address"));

			StringBuilder cityState = new StringBuilder();
			if (!property.isNull("site_city")) {
				cityState.append(property.getString("site_city"));
			}
			if (!property.isNull("site_state")) {
				if (cityState.length() != 0)
					cityState.append(", ");
				cityState.append(property.getString("site_state"));
			}
			if (!property.isNull("site_zip")) {
				if (cityState.length() != 0)
					cityState.append(", ");
				cityState.append(property.getString("site_zip"));
			}

			address2.setText(cityState);

			if (!property.isNull("default_picture")
					&& property.getString("default_picture").length() != 0) {
				FetchPicture fpic = new FetchPicture();
				fpic.execute(property.getString("default_picture"));
			} else {
				propertyImage.setBackground(getResources().getDrawable(
						R.drawable.default_image));
			}

			if (!property.isNull("property_type")) {
				if (!property.getJSONObject("property_type").isNull(
						"virtual_property_type")) {
					propertyType.setText(property.getJSONObject("property_type").getString(
							"virtual_property_type"));
				} else
					propertyType.setText("");
			}
			else{
				propertyType.setText("");
			}

			if (!property.isNull("property_sub_type")) {
				if (!property.getJSONObject("property_sub_type").isNull("name"))
					subType.setText(property.getJSONObject("property_sub_type")
							.getString("name"));
				else {
					subType.setText("");
				}
			} else {
				subType.setText("");
			}

			if (!property.isNull("building_size"))
				propertySize.setText(property.getString("building_size"));
			else
				propertySize.setText("");

			JSONObject propertyDetail = null;
			if (!property.isNull("property_detail_industry")) {
				propertyDetail = property
						.getJSONObject("property_detail_industry");
			} else if (!property.isNull("property_detail_land")) {
				propertyDetail = property.getJSONObject("property_detail_land");
			} else if (!property.isNull("property_detail_multifamily")) {
				propertyDetail = property
						.getJSONObject("property_detail_multifamily");
			} else if (!property.isNull("property_detail_office")) {
				propertyDetail = property
						.getJSONObject("property_detail_office");
			} else if (!property.isNull("property_detail_retail")) {
				propertyDetail = property
						.getJSONObject("property_detail_retail");
			}

			if (propertyDetail.has("building_class")
					&& !propertyDetail.isNull("building_class"))
				buildingClass.setText(propertyDetail.getJSONObject(
						"building_class").getString("name"));
			else
				buildingClass.setText("");

			if (!property.isNull("number_of_units"))
				noOfUnits.setText(Integer.toString(property
						.getInt("number_of_units")));
			else
				noOfUnits.setText("");

			if (!property.isNull("zoning"))
				zoning.setText(property.getString("zoning"));
			else
				zoning.setText("");

			if (propertyDetail.has("parking")
					&& !propertyDetail.isNull("parking"))
				parking.setText(propertyDetail.getString("parking"));
			else
				parking.setText("");

			if (!property.isNull("lot_size"))
				lotSize.setText(property.getString("lot_size"));
			else
				lotSize.setText("");

			if (propertyDetail.has("building_use")
					&& !propertyDetail.isNull("building_use"))
				buildingUse.setText(propertyDetail
						.getJSONObject("building_use").getString("name"));
			else
				buildingUse.setText("");

			if (propertyDetail.has("construction")
					&& !propertyDetail.isNull("construction"))
				construction.setText(propertyDetail.getString("construction"));
			else
				construction.setText("");

			if (!property.isNull("parcel_number"))
				parcelNo.setText(property.getString("parcel_number"));
			else
				parcelNo.setText("");

			if (propertyDetail.has("parking_per_1000")
					&& !propertyDetail.isNull("parking_per_1000"))
				parking1000.setText(propertyDetail
						.getString("parking_per_1000"));
			else
				parking1000.setText("");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Button addNote = (Button) findViewById(R.id.add_note_button);
		addNote.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddNote note = new AddNote(PropertyDetailsActivity.this);
				EditText text = (EditText) findViewById(R.id.note_text);
				note.execute(uriAuthTokenAdded.toString(), text.getText()
						.toString(), propertyId + "", Data.userName);
				text.setText("");
			}
		});

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

	private class FetchPicture extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected void onPreExecute() {
			propertyImage.setBackground(null);
		}

		@Override
		protected Bitmap doInBackground(String... strs) {
			StringBuilder uri = new StringBuilder(URI_INCOMPLETE);
			uri.append(strs[0]);
			Bitmap bmp = null;
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet;

			try {
				httpGet = new HttpGet(uri.toString());
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream imageStream = entity.getContent();
					bmp = BitmapFactory.decodeStream(imageStream);
				} else {
					Log.e("logout", "Failed to fetch image " + statusCode);
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			propertyImage.setImageBitmap(result);
		}

	}
}
