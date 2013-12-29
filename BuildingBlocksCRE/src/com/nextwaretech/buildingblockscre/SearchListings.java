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

public class SearchListings extends AsyncTask<String, Integer, String> {

	private final String QUERY_PARAM = "&q[name_or_site_address_start]=";
	private JSONObject jsonResponse;
	private JSONArray listings;
	private View footerView;
	private Pagination searchedListingsPagination;
	private Activity activity;
	private ListingsArrayAdapter searchedListingsAdapter;

	public SearchListings(Activity activity, View footerView, Pagination page, ListingsArrayAdapter adapter) {
		this.footerView = footerView;
		this.activity = activity;
		searchedListingsAdapter = adapter;
		searchedListingsPagination = page;
	}
	
	@Override
	protected void onPreExecute() {
		Data.loadingMore = true;
		footerView.setVisibility(View.VISIBLE);
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... strs) {

		if (searchedListingsPagination.currentPage == 1
				|| searchedListingsPagination.currentPage <= searchedListingsPagination.totalPages) {
			
			
			Data.loadingMore = true;
			StringBuilder uriRaw = new StringBuilder(strs[0]);
			uriRaw.append(QUERY_PARAM);
			uriRaw.append(strs[1]);

			if (searchedListingsPagination.currentPage != 1) {
				uriRaw.append("&page="
						+ searchedListingsPagination.currentPage);
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
							"Failed to fetch searched listings "+statusCode);
				}

				jsonResponse = new JSONObject(builder.toString());
				pagination = jsonResponse.getJSONObject("pagination");
				searchedListingsPagination.total = pagination
						.getInt("total");
				searchedListingsPagination.totalPages = pagination
						.getInt("total_pages");

				listings = jsonResponse.getJSONArray("listings");

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
		if (searchedListingsPagination.currentPage <= searchedListingsPagination.totalPages) {
			try {
				listings = jsonResponse.getJSONArray("listings");
				for (int i = 0; i < listings.length(); i++) {
					JSONObject listingObj = listings.getJSONObject(i)
							.getJSONObject("listing");
					searchedListingsAdapter.add(listingObj);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if (searchedListingsPagination.currentPage == searchedListingsPagination.totalPages) {
				footerView.setVisibility(View.GONE);
			}
		}
		searchedListingsAdapter.notifyDataSetChanged();
		Data.loadingMore = false;
 
	}
	
}
