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

public class SearchProperties extends AsyncTask<String, Integer, String> {

	private final String QUERY_PARAM = "&q[name_or_site_address_start]=";
	private JSONObject jsonResponse;
	private JSONArray properties;
	private View footerView;
	private Pagination searchedPropertiesPagination;
	private Activity activity;
	private PropertiesArrayAdapter searchedProeprtiesAdapter;

	public SearchProperties(Activity activity, View footerView, Pagination page, PropertiesArrayAdapter adapter) {
		this.footerView = footerView;
		this.activity = activity;
		searchedProeprtiesAdapter = adapter;
		searchedPropertiesPagination = page;
	}
	
	@Override
	protected void onPreExecute() {
		Data.loadingMore = true;
		footerView.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... strs) {

		if (searchedPropertiesPagination.currentPage == 1
				|| searchedPropertiesPagination.currentPage <= searchedPropertiesPagination.totalPages) {
			
			Data.loadingMore = true;
			StringBuilder uriRaw = new StringBuilder(strs[0]);
			uriRaw.append(QUERY_PARAM);
			uriRaw.append(strs[1]);

			if (searchedPropertiesPagination.currentPage != 1) {
				uriRaw.append("&page="
						+ searchedPropertiesPagination.currentPage);
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
							"Failed to fetch searched properties "+statusCode);
				}

				jsonResponse = new JSONObject(builder.toString());
				pagination = jsonResponse.getJSONObject("pagination");
				searchedPropertiesPagination.total = pagination
						.getInt("total");
				searchedPropertiesPagination.totalPages = pagination
						.getInt("total_pages");

				properties = jsonResponse.getJSONArray("properties");

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
		if (searchedPropertiesPagination.currentPage <= searchedPropertiesPagination.totalPages) {
			try {
				properties = jsonResponse.getJSONArray("properties");
				for (int i = 0; i < properties.length(); i++) {
					JSONObject propertyObj = properties.getJSONObject(i)
							.getJSONObject("property");
					searchedProeprtiesAdapter.add(propertyObj);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (searchedPropertiesPagination.currentPage == searchedPropertiesPagination.totalPages) {
				footerView.setVisibility(View.GONE);
			}
		}
		searchedProeprtiesAdapter.notifyDataSetChanged();
		Data.loadingMore = false;
 
	}
}
