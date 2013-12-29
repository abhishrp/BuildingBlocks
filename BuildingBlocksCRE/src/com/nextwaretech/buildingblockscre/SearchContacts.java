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

public class SearchContacts extends AsyncTask<String, Integer, String> {

	private final String QUERY_PARAM = "&q[first_name_or_last_name_or_company_name_start]=";
	private JSONObject jsonResponse;
	private JSONArray contacts;
	private View footerView;
	private Pagination searchedContactsPagination;
	private Activity activity;
	private ContactsArrayAdapter searchedContactsAdapter;

	public SearchContacts(Activity activity, View footerView, Pagination page, ContactsArrayAdapter adapter) {
		this.footerView = footerView;
		this.activity = activity;
		searchedContactsAdapter = adapter;
		searchedContactsPagination = page;
	}
	
	@Override
	protected void onPreExecute() {
		Data.loadingMore = true;
		footerView.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... strs) {

		if (searchedContactsPagination.currentPage == 1
				|| searchedContactsPagination.currentPage <= searchedContactsPagination.totalPages) {
			
			
			Data.loadingMore = true;
			StringBuilder uriRaw = new StringBuilder(strs[0]);
			uriRaw.append(QUERY_PARAM);
			uriRaw.append(strs[1]);

			if (searchedContactsPagination.currentPage != 1) {
				uriRaw.append("&page="
						+ searchedContactsPagination.currentPage);
			}
			
				

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
							"Failed to fetch searched contacts "+statusCode);
				}

				jsonResponse = new JSONObject(builder.toString());
				pagination = jsonResponse.getJSONObject("pagination");
				searchedContactsPagination.total = pagination
						.getInt("total");
				searchedContactsPagination.totalPages = pagination
						.getInt("total_pages");

				contacts = jsonResponse.getJSONArray("contact");

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
		if (searchedContactsPagination.currentPage <= searchedContactsPagination.totalPages) {
			try {
				contacts = jsonResponse.getJSONArray("contact");
				for (int i = 0; i < contacts.length(); i++) {
					JSONObject contactObj = contacts.getJSONObject(i)
							.getJSONObject("contact");

					searchedContactsAdapter.add(contactObj);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (searchedContactsPagination.currentPage == searchedContactsPagination.totalPages) {
				footerView.setVisibility(View.GONE);
			}
		}
		searchedContactsAdapter.notifyDataSetChanged();
		Data.loadingMore = false;
 
	}
}
