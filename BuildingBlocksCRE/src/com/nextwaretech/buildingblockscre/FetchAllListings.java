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

public class FetchAllListings extends AsyncTask<String, Integer, String> {

	private JSONObject jsonResponse;
	private JSONArray listings;
	private JSONObject aListing;
	private View footerView;
	private Pagination allListingsPagination;
	private Activity activity;
	private ListingsArrayAdapter allListingsAdapter;

	public FetchAllListings(Activity activity, View footerView, Pagination page, ListingsArrayAdapter adapter) {
		this.footerView = footerView;
		this.activity = activity;
		allListingsAdapter = adapter;
		allListingsPagination = page;
	}

	@Override
	protected void onPreExecute() {
		Data.loadingMore = true;
		footerView.setVisibility(View.VISIBLE);
		Log.v("damn", "fetching listings");
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... strs) {

		if (allListingsPagination.currentPage == 1
				|| allListingsPagination.currentPage <= allListingsPagination.totalPages) {
			Log.v("listingresp", "all listings");
			Data.loadingMore = true;
			StringBuilder uriRaw = new StringBuilder(strs[0]);

			if (allListingsPagination.currentPage != 1)
				uriRaw.append("&page=" + allListingsPagination.currentPage);

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
					Log.e(AllListingsActivity.class.toString(),
							"Failed to fetch listings "+statusCode);
				}

				jsonResponse = new JSONObject(builder.toString());
				pagination = jsonResponse.getJSONObject("pagination");
				allListingsPagination.total = pagination.getInt("total");
				allListingsPagination.totalPages = pagination.getInt("total_pages");
				allListingsPagination.currentPage = pagination.getInt("current_page");

				Log.v("listingsresp",
						"pagination: " + " total="
								+ pagination.getInt("total")
								+ " total_pages="
								+ pagination.getInt("total_pages")
								+ " current_page="
								+ pagination.getInt("current_page"));

				listings = jsonResponse.getJSONArray("listings");
				for (int i = 0; i < listings.length(); i++) {
					aListing = listings.getJSONObject(i).getJSONObject(
							"listing");
					Log.v("listingsresp",
							i + ") name="
									+ aListing.getString("site_address"));
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

		if (allListingsPagination.currentPage <= allListingsPagination.totalPages) {
			try {
				listings = jsonResponse.getJSONArray("listings");
				for (int i = 0; i < listings.length(); i++) {
					JSONObject listingObj = listings.getJSONObject(i)
							.getJSONObject("listing");
					allListingsAdapter.add(listingObj);
					
				}
				allListingsAdapter.notifyDataSetChanged();

			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (allListingsPagination.currentPage == allListingsPagination.totalPages) {
				footerView.setVisibility(View.GONE);
			}
		}
		Data.loadingMore = false;
	}
}
