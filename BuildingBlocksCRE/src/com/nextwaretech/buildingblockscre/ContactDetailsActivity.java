package com.nextwaretech.buildingblockscre;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ContactDetailsActivity extends Activity {

	private static StringBuilder uriAuthTokenAdded;
	private static final String URI_INCOMPLETE = Data.SERVER_NAME + "contacts/";
	private static int contactId;
	TextView fullName, compName, title, category, address, web, phone,
			altPhone, mobile, fax, email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_details);
		
		TextView headerName = (TextView) findViewById(R.id.header_name);
		headerName.setText("Contacts");
		
		Button contactsTab = (Button) findViewById(R.id.contacts_tab);
		contactsTab.setBackgroundColor(getResources().getColor(R.color.bg));
		
		uriAuthTokenAdded = new StringBuilder(URI_INCOMPLETE);

		fullName = (TextView) findViewById(R.id.full_name);
		compName = (TextView) findViewById(R.id.comp_name);
		title = (TextView) findViewById(R.id.title_value);
		category = (TextView) findViewById(R.id.category_value);
		address = (TextView) findViewById(R.id.address_value);
		web = (TextView) findViewById(R.id.web_value);
		phone = (TextView) findViewById(R.id.phone_value);
		altPhone = (TextView) findViewById(R.id.alt_phone_value);
		mobile = (TextView) findViewById(R.id.mobile_value);
		fax = (TextView) findViewById(R.id.fax_value);
		email = (TextView) findViewById(R.id.email_value);

		Bundle extras = getIntent().getExtras();
		String str = "";
		if (extras != null) {
			str = extras.getString("contactJSONString");
		}
		StringBuilder addr = new StringBuilder();
		JSONObject contactPersonalInfo;
		try {
			JSONObject contact = new JSONObject(str);
			Log.v("contactDetails", contact.getString("name"));
			contactId = contact.getInt("id");
			uriAuthTokenAdded.append(contactId);
			uriAuthTokenAdded.append("/add_note.json?auth_token=");
			uriAuthTokenAdded.append(Data.authToken);
			fullName.setText(contact.getString("name"));

			if (!contact.isNull("company_name"))
				compName.setText(contact.getString("company_name"));
			else
				compName.setText("");

			if (!contact.isNull("title"))
				title.setText(contact.getString("title"));
			else
				title.setText("");

			if (!contact.isNull("category"))
				category.setText(contact.getString("category"));
			else
				category.setText("");

			contactPersonalInfo = contact
					.getJSONObject("contact_personal_info");

			if (!contactPersonalInfo.isNull("address_1")) {
				addr.append(contactPersonalInfo.getString("address_1"));
				addr.append(" ");
			}
			// if(contact_personal_info.getString("address_1")!=null &&
			// contact_personal_info.getString("address_1").length()!=0)

			if (!contactPersonalInfo.isNull("address_2")) {
				addr.append(contactPersonalInfo.getString("address_2"));
				addr.append(" ");
			}

			if (!contactPersonalInfo.isNull("office_city")) {
				addr.append(contactPersonalInfo.getString("office_city"));
				addr.append(" ");
			}

			if (!contactPersonalInfo.isNull("office_state")) {
				addr.append(contactPersonalInfo.getString("office_state"));
				addr.append(" ");
			}

			if (!contactPersonalInfo.isNull("office_zip")) {
				addr.append(contactPersonalInfo.getString("office_zip"));
			}
			address.setText(addr);
			if (!contact.isNull("website"))
				web.setText(contact.getString("website"));
			else
				web.setText("");

			if (!contact.isNull("phone_no"))
				phone.setText(contact.getString("phone_no"));
			else
				phone.setText("");

			if (!contact.isNull("alt_phone_no"))
				altPhone.setText(contact.getString("alt_phone_no"));
			else
				altPhone.setText("");

			if (!contact.isNull("mobile_no"))
				mobile.setText(contact.getString("mobile_no"));
			else
				mobile.setText("");

			if (!contact.isNull("fax"))
				fax.setText(contact.getString("fax"));
			else
				fax.setText("");

			if (!contact.isNull("email"))
				email.setText(contact.getString("email"));
			else
				email.setText("");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		Button addNote = (Button) findViewById(R.id.add_note_button);
		addNote.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddNote note = new AddNote(ContactDetailsActivity.this);
				EditText text = (EditText) findViewById(R.id.note_text);
				Log.v("note", text.getText().toString());
				note.execute(uriAuthTokenAdded.toString(), text.getText()
						.toString(), contactId + "", Data.userName);
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
