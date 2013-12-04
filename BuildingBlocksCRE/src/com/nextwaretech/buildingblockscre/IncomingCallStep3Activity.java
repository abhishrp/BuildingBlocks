package com.nextwaretech.buildingblockscre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.nextwaretech.buildingblockscre.R;

public class IncomingCallStep3Activity extends Activity implements OnScrollListener,
OnItemClickListener, OnQueryTextListener {

	private static final String URI_INCOMING_CALL_NO_AUTH_TOKEN = Data.SERVER_NAME+"incoming_calls.json?auth_token=";
	private static final String URI_CONTACTS_NO_AUTH_TOKEN = Data.SERVER_NAME+"contacts.json?auth_token=";
	private static StringBuilder uriContactsAuthTokenAdded;
	private static StringBuilder uriIncomingCallAuthTokenAdded;
	private static LinkedList<JSONObject> contactsList = new LinkedList<JSONObject>();
	private static LinkedList<JSONObject> searchedContactsList = new LinkedList<JSONObject>();
	private static Pagination allContactsPagination = new Pagination();
	private static Pagination searchedContactsPagination;
	private static boolean searching;
	private static int previousListIndex=-1;
	private String query = "";
	private ListView table;
	private FetchAllContacts fetchContacts;
	private SearchContacts searchContact;

	private ContactsArrayAdapter allContactsAdapter;
	private ContactsArrayAdapter searchedContactsAdapter;
	private View footerView;
	private SearchView search;
	private int contactId;
	private String selectedContactName;
	private int propertyId;
	private int listingId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incoming_call_step3);
		
		Data.loadingMore = false;
		searching = false;
		
		table = (ListView) findViewById(R.id.list);

		allContactsAdapter = new ContactsArrayAdapter(this, this, contactsList, true);
		searchedContactsAdapter = new ContactsArrayAdapter(this, this,
				searchedContactsList, true);

		uriContactsAuthTokenAdded = new StringBuilder(URI_CONTACTS_NO_AUTH_TOKEN);
		uriContactsAuthTokenAdded.append(Data.authToken);
		
		uriIncomingCallAuthTokenAdded = new StringBuilder(URI_INCOMING_CALL_NO_AUTH_TOKEN);
		uriIncomingCallAuthTokenAdded.append(Data.authToken);
		
		footerView = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listfooter, null, false);
		table.addFooterView(footerView);

		Data.loadingMore = true;
		allContactsPagination.currentPage++;
		table.setAdapter(allContactsAdapter);
		fetchContacts = new FetchAllContacts(this, footerView, allContactsPagination, allContactsAdapter);
		fetchContacts.execute(uriContactsAuthTokenAdded.toString());

		Log.v("damn", fetchContacts.getStatus().toString());
		table.setOnScrollListener(this);
		table.setOnItemClickListener(this);

		search = (SearchView) findViewById(R.id.search_bar);
		search.setOnQueryTextListener(this);
		
		Button finish = (Button) findViewById(R.id.in_call_step3_finish);
		finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					IncomingCallActivity.incoming_call_data.put("name", selectedContactName);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				LinkIncomingCall link = new LinkIncomingCall();
				link.execute(uriIncomingCallAuthTokenAdded.toString());
			}
		});
		
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if(query.equals(this.query))
			return false;
		this.query = query;
		searching = true;
		searchedContactsPagination = new Pagination();
		searchedContactsList.clear();
		searchedContactsPagination.currentPage++;
		table.setAdapter(searchedContactsAdapter);
		Log.v("contactresp", "onquerysubmit search page "+ searchedContactsPagination.currentPage);
		//searchContact = new SearchContacts();
		searchContact = new SearchContacts(this, footerView, searchedContactsPagination, searchedContactsAdapter);
		searchContact.execute(uriContactsAuthTokenAdded.toString(), query);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(previousListIndex!=-1) {
			ImageView previousView = (ImageView) parent.getChildAt(previousListIndex).findViewById(R.id.check_mark);
			previousView.setBackgroundResource(R.drawable.checkmark);
		}
		ImageView checkMark = (ImageView)view.findViewById(R.id.check_mark);
		checkMark.setBackgroundResource(R.drawable.checkmark_active);
		previousListIndex = position;
		try {
			contactId = ((JSONObject)table.getItemAtPosition(position)).getInt("id");
			selectedContactName = ((JSONObject)table.getItemAtPosition(position)).getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// what is the bottom item that is visible
				int lastInScreen = firstVisibleItem + visibleItemCount;
				// is the bottom item visible & not loading more already ? Load
				// more !
				if (searching) {
					if ((lastInScreen == totalItemCount) && !Data.loadingMore && searchedContactsPagination.currentPage < searchedContactsPagination.totalPages) {
						searchedContactsPagination.currentPage++;
						Log.v("contactresp", "onscroll search page "+ searchedContactsPagination.currentPage);
						//searchContact = new SearchContacts();
						searchContact = new SearchContacts(this, footerView, searchedContactsPagination, searchedContactsAdapter);
						searchContact.execute(uriContactsAuthTokenAdded.toString(), query);
					}
				} else {
					if ((lastInScreen == totalItemCount) && !Data.loadingMore && allContactsPagination.currentPage < allContactsPagination.totalPages) {
						allContactsPagination.currentPage++;
						Log.v("contactresp", "onscroll all page "+ allContactsPagination.currentPage);
						fetchContacts = new FetchAllContacts(this, footerView, allContactsPagination, allContactsAdapter);
						fetchContacts.execute(uriContactsAuthTokenAdded.toString());
					}
				}
		
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}
	
	
	private class LinkIncomingCall extends AsyncTask<String, Integer, String> {

		private JSONObject jsonResponse;
		
		@Override
		protected String doInBackground(String... strs) {
			String uri = strs[0];
			//JSONObject request = new JSONObject(strs[1]);
			JSONObject incomingCall = new JSONObject();
			JSONObject parent = new JSONObject();
			JSONObject child = new JSONObject();
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();

			HttpPost httpPost;

			try {
				httpPost = new HttpPost(uri);
				
				child.put("id", IncomingCallActivity.incomingCallId);
				child.put("inquiry_class", IncomingCallActivity.inquiry_class);
				parent.put("incoming_call", child);
				if(IncomingCallActivity.inquiry_class.equals("Contact")) {
					parent.put("contact_id", contactId);
				} else if(IncomingCallActivity.inquiry_class.equals("Property")) {
					parent.put("property_id", propertyId);
				} else if(IncomingCallActivity.inquiry_class.equals("Listing")) {
					parent.put("listing_id", listingId);
				}
				httpPost.setEntity(new StringEntity(parent.toString()));
				httpPost.setHeader("REMOTE", "True");
				httpPost.setHeader("Content-type", "application/json");

				HttpResponse response = client.execute(httpPost);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else if(statusCode==403) {
					Intent intent = new Intent("com.nextwaretech.buildingblockscre.SignInActivity");
					startActivity(intent);
				} else {
					Log.e(AllContactsActivity.class.toString(),
							"Failed to fetch contacts "+statusCode);
				}

				jsonResponse = new JSONObject(builder.toString());
				incomingCall = jsonResponse.getJSONObject("incoming_call");
				Log.v("in call", "id "+incomingCall.getInt("id"));
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(!incomingCall.isNull("id"))
					return incomingCall.getInt("id")+"";
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}

}
