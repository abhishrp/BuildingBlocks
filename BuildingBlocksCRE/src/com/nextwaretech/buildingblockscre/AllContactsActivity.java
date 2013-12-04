package com.nextwaretech.buildingblockscre;

import java.util.LinkedList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.nextwaretech.buildingblockscre.R;

public class AllContactsActivity extends Activity implements OnScrollListener,
		OnItemClickListener, OnQueryTextListener {

	private static final String URI_NO_AUTH_TOKEN = Data.SERVER_NAME+"contacts.json?auth_token=";
	private static StringBuilder uriAuthTokenAdded;
	private static LinkedList<JSONObject> contactsList = new LinkedList<JSONObject>();
	private static LinkedList<JSONObject> searchedContactsList = new LinkedList<JSONObject>();
	private static Pagination allContactsPagination = new Pagination();
	private static Pagination searchedContactsPagination;
	private static boolean searching;
	private String query = "";
	private static ListView table;
	private FetchAllContacts fetchContacts;
	private SearchContacts searchContact;

	private ContactsArrayAdapter allContactsAdapter;
	private ContactsArrayAdapter searchedContactsAdapter;
	private View footerView;
	private SearchView search;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_contacts);
		Button contactsTab = (Button) findViewById(R.id.contacts_tab);
		contactsTab.setBackgroundColor(getResources().getColor(R.color.bg));
		
		Data.loadingMore = false;
		searching = false;
		
		table = (ListView) findViewById(R.id.list);

		allContactsAdapter = new ContactsArrayAdapter(this, this, contactsList, false);
		searchedContactsAdapter = new ContactsArrayAdapter(this, this,
				searchedContactsList, false);

		uriAuthTokenAdded = new StringBuilder(URI_NO_AUTH_TOKEN);
		uriAuthTokenAdded.append(Data.authToken);
		Log.v("contacts", uriAuthTokenAdded.toString());

		footerView = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listfooter, null, false);
		table.addFooterView(footerView);

		Data.loadingMore = true;
		allContactsPagination.currentPage++;
		table.setAdapter(allContactsAdapter);
		//FetchAllContacts fetch_contacts = new FetchAllContacts();
		fetchContacts = new FetchAllContacts(this, footerView, allContactsPagination, allContactsAdapter);
		fetchContacts.execute(uriAuthTokenAdded.toString());

		Log.v("damn", fetchContacts.getStatus().toString());
		table.setOnScrollListener(this);
		table.setOnItemClickListener(this);

		search = (SearchView) findViewById(R.id.search_bar);
		search.setOnQueryTextListener(this);
		
		Button incomingCallTab = (Button) findViewById(R.id.incoming_call_tab);
		incomingCallTab.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.nextwaretech.buildingblockscre.IncomingCallActivity");
				startActivity(intent);
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

	@Override
	public void onBackPressed() {
		if(searching)
		{
			searching = false;
			table.setAdapter(allContactsAdapter);
			allContactsAdapter.notifyDataSetChanged();
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
				searchContact = new SearchContacts(this, footerView, searchedContactsPagination, searchedContactsAdapter);
				searchContact.execute(uriAuthTokenAdded.toString(), query);
			}
		} else {
			if ((lastInScreen == totalItemCount) && !Data.loadingMore && allContactsPagination.currentPage < allContactsPagination.totalPages) {
				allContactsPagination.currentPage++;
				Log.v("contactresp", "onscroll all page "+ allContactsPagination.currentPage);
				fetchContacts = new FetchAllContacts(this, footerView, allContactsPagination, allContactsAdapter);
				fetchContacts.execute(uriAuthTokenAdded.toString());
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(
				"com.nextwaretech.buildingblockscre.ContactDetailsActivity");
		Bundle b = new Bundle();
		b.putString("contactJSONString", table.getItemAtPosition(position)
				.toString());
		intent.putExtras(b);
		startActivity(intent);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if(query.length()==0) {
			table.setAdapter(allContactsAdapter);
			allContactsAdapter.notifyDataSetChanged();
		}
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
		searchContact.execute(uriAuthTokenAdded.toString(), query);
		return true;
	}
	/*
	private class FetchAllContacts extends AsyncTask<String, Integer, String> {

		private JSONObject jsonResponse;
		private JSONArray contacts;
		private JSONObject a_contact;

		@Override
		protected void onPreExecute() {
			loadingMore = true;
			footerView.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... strs) {

			if (allContactsPagination.current_page == 1
					|| allContactsPagination.current_page <= allContactsPagination.total_pages) {
				Log.v("contactresp", "all contacts");
				loadingMore = true;
				StringBuilder uri_raw = new StringBuilder(strs[0]);

				if (allContactsPagination.current_page != 1)
					uri_raw.append("&page=" + allContactsPagination.current_page);

				String uri = uri_raw.toString();

				StringBuilder builder = new StringBuilder();
				HttpClient client = new DefaultHttpClient();

				JSONObject pagination;

				HttpGet httpGet;

				try {
					httpGet = new HttpGet(uri);

					httpGet.setHeader("REMOTE", "True");
					httpGet.setHeader("Content-type", "application/json");

					HttpResponse response = client.execute(httpGet);
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
					pagination = jsonResponse.getJSONObject("pagination");
					allContactsPagination.total = pagination.getInt("total");
					allContactsPagination.total_pages = pagination.getInt("total_pages");
					allContactsPagination.current_page = pagination.getInt("current_page");

					Log.v("contactresp",
							"pagination: " + " total="
									+ pagination.getInt("total")
									+ " total_pages="
									+ pagination.getInt("total_pages")
									+ " current_page="
									+ pagination.getInt("current_page"));

					contacts = jsonResponse.getJSONArray("contact");
					for (int i = 0; i < contacts.length(); i++) {
						a_contact = contacts.getJSONObject(i).getJSONObject(
								"contact");
						Log.v("contactresp",
								i + ") name="
										+ a_contact.getString("first_name"));
					}

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
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			loadingMore = true;

			if (allContactsPagination.current_page <= allContactsPagination.total_pages) {
				try {
					contacts = jsonResponse.getJSONArray("contact");
					for (int i = 0; i < contacts.length(); i++) {
						JSONObject contactObj = contacts.getJSONObject(i)
								.getJSONObject("contact");
						contactsList.addLast(contactObj);
					}
					allContactsAdapter.notifyDataSetChanged();

				} catch (JSONException e) {
					e.printStackTrace();
				}

				if (allContactsPagination.current_page == allContactsPagination.total_pages) {
					footerView.setVisibility(View.GONE);
				}
			}
			loadingMore = false;
		}
	}*/
	/*
	private class SearchContacts extends AsyncTask<String, Integer, String> {

		private final String queryParam = "&q[first_name_or_last_name_or_company_name_start]=";
		private JSONObject jsonResponse;
		private JSONArray contacts;
		private JSONObject a_contact;

		@Override
		protected void onPreExecute() {
			Data.loadingMore = true;
			footerView.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... strs) {

			if (searchedContactsPagination.current_page == 1
					|| searchedContactsPagination.current_page <= searchedContactsPagination.total_pages) {
				
				
				Log.v("contactresp", "search contacts");
				Data.loadingMore = true;
				StringBuilder uri_raw = new StringBuilder(strs[0]);
				uri_raw.append(queryParam);
				uri_raw.append(strs[1]);

				if (searchedContactsPagination.current_page != 1) {
					uri_raw.append("&page="
							+ searchedContactsPagination.current_page);
				}
				
					

				String uri = uri_raw.toString();

				StringBuilder builder = new StringBuilder();
				HttpClient client = new DefaultHttpClient();

				JSONObject pagination;

				HttpGet httpGet;

				try {
					httpGet = new HttpGet(uri);

					httpGet.setHeader("REMOTE", "True");
					httpGet.setHeader("Content-type", "application/json");

					HttpResponse response = client.execute(httpGet);
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
								"Failed to fetch searched contacts "+statusCode);
					}

					jsonResponse = new JSONObject(builder.toString());
					pagination = jsonResponse.getJSONObject("pagination");
					searchedContactsPagination.total = pagination
							.getInt("total");
					searchedContactsPagination.total_pages = pagination
							.getInt("total_pages");
					//searchedContactsPagination.current_page = pagination.getInt("current_page");

					Log.v("contactresp",
							"pagination: " + " total="
									+ pagination.getInt("total")
									+ " total_pages="
									+ pagination.getInt("total_pages")
									+ " current_page="
									+ pagination.getInt("current_page"));

					contacts = jsonResponse.getJSONArray("contact");
					for (int i = 0; i < contacts.length(); i++) {
						a_contact = contacts.getJSONObject(i).getJSONObject(
								"contact");
						Log.v("contactresp",
								i + ") name="
										+ a_contact.getString("first_name"));
					}

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
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Data.loadingMore = true;
			if (searchedContactsPagination.current_page <= searchedContactsPagination.total_pages) {
				try {
					contacts = jsonResponse.getJSONArray("contact");
					for (int i = 0; i < contacts.length(); i++) {
						JSONObject contactObj = contacts.getJSONObject(i)
								.getJSONObject("contact");

						searchedContactsList.addLast(contactObj);
					}
					Log.v("contactresp", "list size "+ searchedContactsList.size() +" page " + searchedContactsPagination.current_page);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				if (searchedContactsPagination.current_page == searchedContactsPagination.total_pages) {
					footerView.setVisibility(View.GONE);
				}
			}
			searchedContactsAdapter.notifyDataSetChanged();
			Data.loadingMore = false;
	 
		}
	}
	*/

}
