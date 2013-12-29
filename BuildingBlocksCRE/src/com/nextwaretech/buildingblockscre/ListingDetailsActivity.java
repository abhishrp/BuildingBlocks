package com.nextwaretech.buildingblockscre;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ListingDetailsActivity extends Activity {
	private static StringBuilder uriAuthTokenAdded;
	private static final String URI_INCOMPLETE = Data.SERVER_NAME + "listings/";
	private static int listingId;
	private TextView status, project_name, address, team, property_type,
			listing_type, property_size, unit_avail, primary_client,
			expiration_date, start_date, sale_price, lease_rate,
			listing_commission, co_op_commission, primary_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listing_details);

		TextView headerName = (TextView) findViewById(R.id.header_name);
		headerName.setText("Listings");

		Button properties_tab = (Button) findViewById(R.id.listing_tab);
		properties_tab.setBackgroundColor(getResources().getColor(R.color.bg));

		uriAuthTokenAdded = new StringBuilder(URI_INCOMPLETE);

		status = (TextView) findViewById(R.id.status_value);
		project_name = (TextView) findViewById(R.id.project_name_value);
		address = (TextView) findViewById(R.id.address_value);
		team = (TextView) findViewById(R.id.team_value);
		property_type = (TextView) findViewById(R.id.property_type_value);
		listing_type = (TextView) findViewById(R.id.listing_type_value);
		property_size = (TextView) findViewById(R.id.property_size_value);
		unit_avail = (TextView) findViewById(R.id.unit_avail_value);
		primary_client = (TextView) findViewById(R.id.primary_client_value);
		expiration_date = (TextView) findViewById(R.id.expiration_date_value);
		start_date = (TextView) findViewById(R.id.start_date_value);
		sale_price = (TextView) findViewById(R.id.sale_price_value);
		lease_rate = (TextView) findViewById(R.id.lease_rate_value);
		listing_commission = (TextView) findViewById(R.id.listing_comm_value);
		co_op_commission = (TextView) findViewById(R.id.co_op_comm_value);
		primary_contact = (TextView) findViewById(R.id.primary_contact_value);
		
		
		
		Bundle extras = getIntent().getExtras();
		String str = "";
		if (extras != null) {
			str = extras.getString("listingJSONString");
		}

		try {
			JSONObject listing = new JSONObject(str);
			listingId = listing.getInt("id");
			uriAuthTokenAdded.append("/");
			uriAuthTokenAdded.append(listingId);
			uriAuthTokenAdded.append("/new_notes.json?auth_token=");
			uriAuthTokenAdded.append(Data.authToken);

			
			if (!listing.isNull("listing_status")) {
				if (!listing.getJSONObject("listing_status").isNull(
						"name")) {
					status.setText(listing.getJSONObject("listing_status").getString(
							"name"));
				} else
					status.setText("");
			} else {
				status.setText("");
			}

			if (!listing.isNull("project_name")) {
					project_name.setText(listing.getString("project_name"));
			} else {
				project_name.setText("");
			}
			
			
			StringBuilder site_address = new StringBuilder(listing.getString("site_address"));
			StringBuilder cityState = new StringBuilder();
			if (!listing.isNull("site_city")) {
				cityState.append(listing.getString("site_city"));
			}
			if (!listing.isNull("site_state")) {
				if (cityState.length() != 0)
					cityState.append(", ");
				cityState.append(listing.getString("site_state"));
			}
			if (!listing.isNull("site_zip")) {
				if (cityState.length() != 0)
					cityState.append(", ");
				cityState.append(listing.getString("site_zip"));
			}
			if(cityState.length()!=0){
				site_address.append(",");
				site_address.append(cityState);
			}
			address.setText(site_address.toString());
			
			
			if (!listing.isNull("team_name")) {
					team.setText(listing.getString("team_name"));
			} else {
				team.setText("");
			}
			
			if (!listing.isNull("property")) {
				JSONObject property = listing.getJSONObject("property");
				if (!property.getJSONObject("property_type").isNull(
						"virtual_property_type")) {
					property_type.setText(property.getJSONObject("property_type").getString(
							"virtual_property_type"));
				} else
					property_type.setText("");
			} else {
				property_type.setText("");
			}

			
			if (!listing.isNull("listing_details")) {
				JSONObject details = listing.getJSONObject("listing_details");
				if (!details.isNull("listing_type")) {
					listing_type.setText(details.getString("listing_type"));
				} else
					listing_type.setText("");
			} else {
				listing_type.setText("");
			}
			
			
			if (!listing.isNull("property")) {
				JSONObject property = listing.getJSONObject("property");
				if (!property.isNull("building_size")) {
					property_size.setText(property.getString("building"));
				} else
					property_size.setText("");
			} else {
				property_size.setText("");
			}

			
			if (!listing.isNull("number_of_units")) {
					unit_avail.setText(listing.getInt("units"));
			} else {
				unit_avail.setText("");
			}
			
			
			primary_client.setText("");
			
			
			if (!listing.isNull("listing_details")) {
				JSONObject details = listing.getJSONObject("listing_details");
				if (!details.isNull("listing_expiration")) {
					expiration_date.setText(details.getString("listing_expiration"));
				} else
					expiration_date.setText("");
			} else {
				expiration_date.setText("");
			}
			
			if (!listing.isNull("listing_details")) {
				JSONObject details = listing.getJSONObject("listing_details");
				if (!details.isNull("listing_start_date")) {
					start_date.setText(details.getString("listing_start_date"));
				} else
					start_date.setText("");
			} else {
				start_date.setText("");
			}
			
			if (!listing.isNull("listing_sale_info")) {
				JSONObject sale = listing.getJSONObject("listing_sale_info");
				if (!sale.isNull("list_price")) {
					sale_price.setText(sale.getString("list_price"));
				} else
					sale_price.setText("");
			} else {
				sale_price.setText("");
			}
			
			if (!listing.isNull("listing_lease_info")) {
				JSONObject lease = listing.getJSONObject("listing_lease_info");
				if (!lease.isNull("lease_rate")) {
					lease_rate.setText(lease.getString("lease_rate"));
				} else
					lease_rate.setText("");
			} else {
				lease_rate.setText("");
			}
			
			if (!listing.isNull("listing_agent_commission")) {
				JSONObject listcomm = listing.getJSONObject("listing_agent_commission");
				if (!listcomm.isNull("sale_commission")) {
					listing_commission.setText(listcomm.getString("sale_commission"));
				} else
					listing_commission.setText("");
			} else {
				listing_commission.setText("");
			}
			
			if (!listing.isNull("coop_agent_commission")) {
				JSONObject coopcomm = listing.getJSONObject("coop_agent_commission");
				if (!coopcomm.isNull("sale_commission")) {
					co_op_commission.setText(coopcomm.getString("sale_commission"));
				} else
					co_op_commission.setText("");
			} else {
				co_op_commission.setText("");
			}
			
			if (!listing.isNull("contact")) {
				JSONObject contact = listing.getJSONObject("contact");
				if (!contact.isNull("name")) {
					primary_contact.setText(contact.getString("name"));
				} else
					primary_contact.setText("");
			} else {
				primary_contact.setText("");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Button addNote = (Button) findViewById(R.id.add_note_button);
		addNote.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddNote note = new AddNote(ListingDetailsActivity.this);
				EditText text = (EditText) findViewById(R.id.note_text);
				note.execute(uriAuthTokenAdded.toString(), text.getText()
						.toString(), listingId + "", Data.userName);
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

}
