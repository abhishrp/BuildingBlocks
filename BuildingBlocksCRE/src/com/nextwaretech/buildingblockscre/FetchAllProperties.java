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

public class FetchAllProperties extends AsyncTask<String, Integer, String> {

	private JSONObject jsonResponse;
	private JSONArray properties;
	private JSONObject aProperty;
	private View footerView;
	private Pagination allPropertiesPagination;
	private Activity activity;
	private PropertiesArrayAdapter allPropertiesAdapter;

	public FetchAllProperties(Activity activity, View footerView, Pagination page, PropertiesArrayAdapter adapter) {
		this.footerView = footerView;
		this.activity = activity;
		allPropertiesAdapter = adapter;
		allPropertiesPagination = page;
	}
	
	@Override
	protected void onPreExecute() {
		Data.loadingMore = true;
		footerView.setVisibility(View.VISIBLE);
		Log.v("damn", "fetching properties");
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... strs) {

		if (allPropertiesPagination.currentPage == 1
				|| allPropertiesPagination.currentPage <= allPropertiesPagination.totalPages) {
			Log.v("propertieresp", "all properties");
			Data.loadingMore = true;
			StringBuilder uriRaw = new StringBuilder(strs[0]);

			if (allPropertiesPagination.currentPage != 1)
				uriRaw.append("&page=" + allPropertiesPagination.currentPage);

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
					Log.e(AllPropertiesActivity.class.toString(),
							"Failed to fetch properties "+statusCode);
					return statusCode+"";
				}

				jsonResponse = new JSONObject(builder.toString());
				pagination = jsonResponse.getJSONObject("pagination");
				allPropertiesPagination.total = pagination.getInt("total");
				allPropertiesPagination.totalPages = pagination.getInt("total_pages");
				allPropertiesPagination.currentPage = pagination.getInt("current_page");

				Log.v("propertiesresp",
						"pagination: " + " total="
								+ pagination.getInt("total")
								+ " total_pages="
								+ pagination.getInt("total_pages")
								+ " current_page="
								+ pagination.getInt("current_page"));

				properties = jsonResponse.getJSONArray("properties");
				for (int i = 0; i < properties.length(); i++) {
					aProperty = properties.getJSONObject(i).getJSONObject(
							"property");
					Log.v("propertiesresp",
							i + ") name="
									+ aProperty.getString("site_address"));
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
		if(Integer.parseInt(result)!=200){
			footerView.setVisibility(View.GONE);
			Data.loadingMore = false;
			return;
		}
		Data.loadingMore = true;

		if (allPropertiesPagination.currentPage <= allPropertiesPagination.totalPages) {
			try {
				properties = jsonResponse.getJSONArray("properties");
				for (int i = 0; i < properties.length(); i++) {
					JSONObject propertyObj = properties.getJSONObject(i)
							.getJSONObject("property");
					allPropertiesAdapter.add(propertyObj);
					
				}
				allPropertiesAdapter.notifyDataSetChanged();

			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (allPropertiesPagination.currentPage == allPropertiesPagination.totalPages) {
				footerView.setVisibility(View.GONE);
			}
		}
		Data.loadingMore = false;
	}
}
