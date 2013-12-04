package com.nextwaretech.buildingblockscre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class FetchAllContacts extends AsyncTask<String, Integer, String> {

	private JSONObject jsonResponse;
	private JSONArray contacts;
	private JSONObject aContact;
	private View footerView;
	private Pagination allContactsPagination;
	private Activity activity;
	private ContactsArrayAdapter allContactsAdapter;

	public FetchAllContacts(Activity activity, View footerView, Pagination page, ContactsArrayAdapter adapter) {
		this.footerView = footerView;
		this.activity = activity;
		allContactsAdapter = adapter;
		allContactsPagination = page;
	}
	
	@Override
	protected void onPreExecute() {
		Data.loadingMore = true;
		footerView.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... strs) {

		if (allContactsPagination.currentPage == 1
				|| allContactsPagination.currentPage <= allContactsPagination.totalPages) {
			Log.v("contactresp", "all contacts");
			Data.loadingMore = true;
			StringBuilder uriRaw = new StringBuilder(strs[0]);

			if (allContactsPagination.currentPage != 1)
				uriRaw.append("&page=" + allContactsPagination.currentPage);

			String uri = uriRaw.toString();

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
					activity.startActivity(intent);
				} else {
					Log.e(AllContactsActivity.class.toString(),
							"Failed to fetch contacts "+statusCode);
				}

				jsonResponse = new JSONObject(builder.toString());
				pagination = jsonResponse.getJSONObject("pagination");
				allContactsPagination.total = pagination.getInt("total");
				allContactsPagination.totalPages = pagination.getInt("total_pages");
				allContactsPagination.currentPage = pagination.getInt("current_page");

				Log.v("contactresp",
						"pagination: " + " total="
								+ pagination.getInt("total")
								+ " total_pages="
								+ pagination.getInt("total_pages")
								+ " current_page="
								+ pagination.getInt("current_page"));

				contacts = jsonResponse.getJSONArray("contact");
				for (int i = 0; i < contacts.length(); i++) {
					aContact = contacts.getJSONObject(i).getJSONObject(
							"contact");
					Log.v("contactresp",
							i + ") name="
									+ aContact.getString("first_name"));
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

		if (allContactsPagination.currentPage <= allContactsPagination.totalPages) {
			try {
				contacts = jsonResponse.getJSONArray("contact");
				for (int i = 0; i < contacts.length(); i++) {
					JSONObject contactObj = contacts.getJSONObject(i)
							.getJSONObject("contact");
					//contactsList.addLast(contactObj);
					allContactsAdapter.add(contactObj);
					
				}
				allContactsAdapter.notifyDataSetChanged();

			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (allContactsPagination.currentPage == allContactsPagination.totalPages) {
				footerView.setVisibility(View.GONE);
			}
		}
		Data.loadingMore = false;
	}
}