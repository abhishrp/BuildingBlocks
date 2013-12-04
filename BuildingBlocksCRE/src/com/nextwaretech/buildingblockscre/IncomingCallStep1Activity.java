package com.nextwaretech.buildingblockscre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.nextwaretech.buildingblockscre.R;

public class IncomingCallStep1Activity extends Activity {

	private static final String URI_ALL_STATES_NO_AUTH_TOKEN = Data.SERVER_NAME+"states.json?auth_token=";
	private static final String URI_CALLER_TYPES_NO_AUTH_TOKEN = Data.SERVER_NAME+"caller_types.json?auth_token=";
	private static final String URI_LISITING_SOURCE_NO_AUTH_TOKEN = Data.SERVER_NAME+"listing_source.json?auth_token=";
	private static final String URI_INCOMING_CALLS_NO_AUTH_TOKEN = Data.SERVER_NAME+"incoming_calls.json?auth_token=";
	private static StringBuilder uriAllStates;
	private static StringBuilder uriCallerType;
	private static StringBuilder uriListingSource;
	private static StringBuilder uriIncomingCalls;
	private static HashMap<String, Integer> callerTypeCodes;
	private static HashMap<String, Integer> listingSourceCodes;
	private Context context = this;
	private TextView firstNameTv;
	private TextView noteTv;
	private EditText firstNameEt;
	private EditText noteTextEt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incoming_call_step1);

		uriAllStates = new StringBuilder(URI_ALL_STATES_NO_AUTH_TOKEN);
		uriAllStates.append(Data.authToken);
		GetAllStates states = new GetAllStates(this);
		states.execute(uriAllStates.toString());
		
		uriCallerType = new StringBuilder(URI_CALLER_TYPES_NO_AUTH_TOKEN);
		uriCallerType.append(Data.authToken);
		GetCallerTypes callerTypes = new GetCallerTypes(this);
		callerTypes.execute(uriCallerType.toString());
		
		uriListingSource = new StringBuilder(URI_LISITING_SOURCE_NO_AUTH_TOKEN);
		uriListingSource.append(Data.authToken);
		GetListingSource listingSource = new GetListingSource(this);
		listingSource.execute(uriListingSource.toString());
		
		uriIncomingCalls = new StringBuilder(URI_INCOMING_CALLS_NO_AUTH_TOKEN);
		uriIncomingCalls.append(Data.authToken);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		System.out.println(dateFormat.format(date));

		EditText dateET = (EditText) findViewById(R.id.in_call_date);
		dateET.setText(dateFormat.format(date));

		firstNameTv = (TextView) findViewById(R.id.in_call_first_name_tv);
		noteTv = (TextView) findViewById(R.id.note);
		firstNameEt = (EditText)findViewById(R.id.in_call_first_name);
		noteTextEt = (EditText)findViewById(R.id.note_text);
		
		//JSONObject request = new JSONObject();
		//final JSONObject incoming_call = new JSONObject();
		
		Button step1ContinueButton = (Button) findViewById(R.id.in_call_step1_continue);
		step1ContinueButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String firstName =firstNameEt.getText().toString(); 
				String noteText = noteTextEt.getText().toString();
				JSONObject incomingCall = IncomingCallActivity.incoming_call_data;
				if(firstName!=null && firstName.length()!=0 && noteText!=null & noteText.length()!=0) {
					try {
						Log.v("in call", "creating request");
						incomingCall.put("date", ((EditText)findViewById(R.id.in_call_date)).getText().toString());
						incomingCall.put("first_name", ((EditText)findViewById(R.id.in_call_first_name)).getText().toString());
						incomingCall.put("last_name", ((EditText)findViewById(R.id.in_call_last_name)).getText().toString());
						incomingCall.put("company", ((EditText)findViewById(R.id.in_call_comp_name)).getText().toString());
						incomingCall.put("address_1", ((EditText)findViewById(R.id.in_call_address1)).getText().toString());
						incomingCall.put("address_2", ((EditText)findViewById(R.id.in_call_address2)).getText().toString());
						incomingCall.put("city", ((EditText)findViewById(R.id.in_call_city)).getText().toString());
						incomingCall.put("state", "choose_type_of_link");
						incomingCall.put("phone", ((EditText)findViewById(R.id.in_call_phone)).getText().toString());
						incomingCall.put("cell", ((EditText)findViewById(R.id.in_call_cell)).getText().toString());
						incomingCall.put("fax", ((EditText)findViewById(R.id.in_call_fax)).getText().toString());
						incomingCall.put("alt_phone", ((EditText)findViewById(R.id.in_call_alt_phone)).getText().toString());
						incomingCall.put("email", ((EditText)findViewById(R.id.in_call_email)).getText().toString());
						incomingCall.put("caller_type_id", callerTypeCodes.get(((Spinner)findViewById(R.id.in_call_caller_type)).getSelectedItem().toString()));
						incomingCall.put("found_listing_by_id", listingSourceCodes.get(((Spinner)findViewById(R.id.in_call_found_listing_by)).getSelectedItem().toString()));
						incomingCall.put("title", ((EditText)findViewById(R.id.in_call_title)).getText().toString());
						incomingCall.put("site_state", ((Spinner)findViewById(R.id.in_call_state)).getSelectedItem().toString());
						incomingCall.put("zip", ((EditText)findViewById(R.id.in_call_zip)).getText().toString());
						incomingCall.put("notes", ((EditText)findViewById(R.id.note_text)).getText().toString());
						incomingCall.put("inquiry_status_id", "1001");
						incomingCall.put("name", "");
						
						AddIncomingCall inCall = new AddIncomingCall(context);
						inCall.execute(uriIncomingCalls.toString(), incomingCall.toString());
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(context, "The fields 'First name' and 'Note' cannot be empty", Toast.LENGTH_LONG).show();
					firstNameTv.setTextColor(ColorStateList.valueOf(Color.RED));
					firstNameTv.setTypeface(null, Typeface.BOLD);
					noteTv.setTextColor(ColorStateList.valueOf(Color.RED));
					noteTv.setTypeface(null, Typeface.BOLD);
				}
			}
		});
	}
	
	
	private class GetAllStates extends AsyncTask<String, Integer, String> {

		private Context context;
		private JSONObject jsonResponse;
		private JSONArray states;
		
		public GetAllStates(Context context) {
			this.context = context;
		}
		
		@Override
		protected String doInBackground(String... strs) {
			String uri = strs[0];
			
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();

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
				states = jsonResponse.getJSONArray("states");
				Log.v("states", states.toString());
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
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			try {
				List<String> spinnerArray = new ArrayList<String>();
				for(int i=0; i<states.length(); i++) {
					spinnerArray.add(states.getString(i));
				}
				Spinner stateSpinner = (Spinner) findViewById(R.id.in_call_state);
				ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray);
				stateSpinner.setAdapter(spinnerArrayAdapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class GetCallerTypes extends AsyncTask<String, Integer, String> {

		private Context context;
		private JSONObject jsonResponse;
		private JSONArray callerTypes;
		
		public GetCallerTypes(Context context) {
			this.context = context;
		}
		
		@Override
		protected String doInBackground(String... strs) {
			String uri = strs[0];
			
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();

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
				callerTypes = jsonResponse.getJSONArray("callertypes");
				callerTypeCodes = new HashMap<String, Integer>();
				for(int i=0; i<callerTypes.length(); i++) {
					callerTypeCodes.put(callerTypes.getJSONArray(i).getString(0), callerTypes.getJSONArray(i).getInt(1));
				}
				Log.v("states", callerTypes.toString());
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
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			//try {
				Object[] objArray = callerTypeCodes.keySet().toArray();
				String[] spinnerArray = Arrays.copyOf(objArray, objArray.length, String[].class);
				/*List<String> spinnerArray = new ArrayList<String>();
				for(int i=0; i<callerTypes.length(); i++) {
					spinnerArray.add(callerTypes.getJSONArray(i).getString(0));
				}*/
				Spinner stateSpinner = (Spinner) findViewById(R.id.in_call_caller_type);
				ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray);
				stateSpinner.setAdapter(spinnerArrayAdapter);
			/*} catch (JSONException e) {
				e.printStackTrace();
			}*/
			
		}
		
	}
	
	private class GetListingSource extends AsyncTask<String, Integer, String> {

		private Context context;
		private JSONObject jsonResponse;
		private JSONArray listingSources;
		
		public GetListingSource(Context context) {
			this.context = context;
		}
		
		@Override
		protected String doInBackground(String... strs) {
			String uri = strs[0];
			
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();

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
				listingSources = jsonResponse.getJSONArray("listing_source");
				listingSourceCodes = new HashMap<String, Integer>();
				for(int i=0; i<listingSources.length(); i++) {
					listingSourceCodes.put(listingSources.getJSONArray(i).getString(0), listingSources.getJSONArray(i).getInt(1));
				}
				Log.v("states", listingSources.toString());
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
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			//try {
				Object[] objArray = listingSourceCodes.keySet().toArray();
				String[] spinnerArray = Arrays.copyOf(objArray, objArray.length, String[].class);
				/*List<String> spinnerArray = new ArrayList<String>();
				for(int i=0; i<callerTypes.length(); i++) {
					spinnerArray.add(callerTypes.getJSONArray(i).getString(0));
				}*/
				Spinner stateSpinner = (Spinner) findViewById(R.id.in_call_found_listing_by);
				ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray);
				stateSpinner.setAdapter(spinnerArrayAdapter);
			/*} catch (JSONException e) {
				e.printStackTrace();
			}*/
		}
		
	}
	
	private class AddIncomingCall extends AsyncTask<String, Integer, String> {
		private Context context;
		private JSONObject jsonResponse;
		
		public AddIncomingCall(Context context) {
			this.context = context;
		}
		
		@Override
		protected String doInBackground(String... strs) {
			String uri = strs[0];
			//JSONObject request = new JSONObject(strs[1]);
			JSONObject incomingCall = new JSONObject();
			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();

			HttpPost httpPost;

			try {
				httpPost = new HttpPost(uri);

				httpPost.setEntity(new StringEntity(strs[1]));
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
				if(incomingCall.has("id"))
					Log.v("in call", "id "+incomingCall.getString("id"));
				
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
		
		@Override
		protected void onPostExecute(String result) {
			if(result!=null) {
				Activity tabs = getParent();
				IncomingCallActivity.incomingCallId = Integer.parseInt(result);
				((TabHost)tabs.findViewById(android.R.id.tabhost)).setCurrentTab(1);
			}
		}
		
	}
}
