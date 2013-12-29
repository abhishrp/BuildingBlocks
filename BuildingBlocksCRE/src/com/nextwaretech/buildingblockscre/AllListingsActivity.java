package com.nextwaretech.buildingblockscre;

import java.util.LinkedList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TableRow;
import android.widget.TextView;

import com.nextwaretech.buildingblockscre.R;

public class AllListingsActivity extends Activity implements OnScrollListener,
		OnItemClickListener, OnQueryTextListener {

	private static final String URI_NO_AUTH_TOKEN = Data.SERVER_NAME+"listings.json?auth_token=";
	private static StringBuilder uriAuthTokenAdded;
	private String query = "";
	private static boolean searching;

	private static LinkedList<JSONObject> lisingsList = new LinkedList<JSONObject>();
	private static LinkedList<JSONObject> searchedListingsList = new LinkedList<JSONObject>();

	private static Pagination allListingsPagination = new Pagination();
	private static Pagination searchedListingsPagination;

	private ListingsArrayAdapter allListingsAdapter;
	private ListingsArrayAdapter searchedListingsAdapter;

	private FetchAllListings fetchListings;
	private SearchListings searchListing;

	private View footerView;
	private SearchView search;
	private static ListView table;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_listings);
		
		TextView headerName = (TextView) findViewById(R.id.header_name);
		headerName.setText("Listings");

		Button contactsTab = (Button) findViewById(R.id.listing_tab);
		contactsTab.setBackgroundColor(getResources().getColor(R.color.bg));

		TextView col1 = (TextView) findViewById(R.id.table_col1);
		col1.setText(getResources().getString(R.string.address));
		col1.setLayoutParams(new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 2f));
		TextView col2 = (TextView) findViewById(R.id.table_col2);
		col2.setText(getResources().getString(R.string.type));
		col2.setLayoutParams(new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 2f));
		TextView col3 = (TextView) findViewById(R.id.table_col3);
		col3.setText(getResources().getString(R.string.property_type));
		col3.setLayoutParams(new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
		TextView col4 = (TextView) findViewById(R.id.table_col4);
		col4.setText(getResources().getString(R.string.team));
		col4.setLayoutParams(new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));
		TextView col5 = (TextView) findViewById(R.id.table_col5);
		col5.setText(getResources().getString(R.string.status));
		col5.setLayoutParams(new TableRow.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f));

		Data.loadingMore = false;
		searching = false;

		table = (ListView) findViewById(R.id.list);

		allListingsAdapter = new ListingsArrayAdapter(this, this,
				lisingsList, false);
		searchedListingsAdapter = new ListingsArrayAdapter(this, this,
				searchedListingsList, false);

		uriAuthTokenAdded = new StringBuilder(URI_NO_AUTH_TOKEN);
		uriAuthTokenAdded.append(Data.authToken);

		footerView = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listfooter, null, false);
		table.addFooterView(footerView);

		Data.loadingMore = true;
		allListingsPagination.currentPage++;
		table.setAdapter(allListingsAdapter);
		fetchListings = new FetchAllListings(this,
				footerView, allListingsPagination, allListingsAdapter);
		fetchListings.execute(uriAuthTokenAdded.toString());

		table.setOnScrollListener(this);
		table.setOnItemClickListener(this);

		search = (SearchView) findViewById(R.id.search_bar);
		search.setOnQueryTextListener(this);
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
		if (searching) {
			searching = false;
			table.setAdapter(allListingsAdapter);
			allListingsAdapter.notifyDataSetChanged();
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
			if ((lastInScreen == totalItemCount)
					&& !Data.loadingMore
					&& searchedListingsPagination.currentPage < searchedListingsPagination.totalPages) {
				searchedListingsPagination.currentPage++;
				searchListing = new SearchListings(this, footerView,
						searchedListingsPagination, searchedListingsAdapter);
				searchListing.execute(uriAuthTokenAdded.toString(), query);
			}
		} else {
			if ((lastInScreen == totalItemCount)
					&& !Data.loadingMore
					&& allListingsPagination.currentPage < allListingsPagination.totalPages) {
				allListingsPagination.currentPage++;
				fetchListings = new FetchAllListings(this, footerView,
						allListingsPagination, allListingsAdapter);
				fetchListings.execute(uriAuthTokenAdded.toString());
			}
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(
				"com.nextwaretech.buildingblockscre.ListingDetailsActivity");
		Bundle b = new Bundle();
		b.putString("listingJSONString", table.getItemAtPosition(position)
				.toString());
		intent.putExtras(b);
		startActivity(intent);
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if(query.length()==0) {
			table.setAdapter(allListingsAdapter);
			allListingsAdapter.notifyDataSetChanged();
		}
		if(query.equals(this.query))
			return false;
		this.query = query;
		searching = true;
		searchedListingsPagination = new Pagination();
		searchedListingsList.clear();
		searchedListingsPagination.currentPage++;
		table.setAdapter(searchedListingsAdapter);
		searchListing = new SearchListings(this, footerView, searchedListingsPagination, searchedListingsAdapter);
		searchListing.execute(uriAuthTokenAdded.toString(), query);
		return true;
	}
	
}
