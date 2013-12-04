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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AddNote extends AsyncTask<String, Integer, String> {

	private JSONObject jsonResponse;
	private Context context;
	
	public AddNote(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... strs) {

		String result = "";
		Log.v("contactresp", "adding note");
		String uri = strs[0];

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		JSONObject parentData = new JSONObject();
		JSONObject childData = new JSONObject();
		JSONArray message = new JSONArray();
		HttpPost httpPost;

		try {
			httpPost = new HttpPost(uri);

			childData.put("note", strs[1]);
			childData.put("id", strs[2]);
			childData.put("created_by", strs[3]);
			parentData.put("note", childData);

			httpPost.setEntity(new StringEntity(parentData.toString()));

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
				result = "note added successfully";
			} else if (statusCode == 403) {
				Intent intent = new Intent(
						"com.nextwaretech.buildingblockscre.SignInActivity");
				context.startActivity(intent);
			} else {
				result = "Failed to add note ";
				Log.e(ContactDetailsActivity.class.toString(),
						"Failed to add note " + statusCode);
			}

			jsonResponse = new JSONObject(builder.toString());
			message = jsonResponse.getJSONArray("message");

			Log.v("contactnote", message.get(0).toString());

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
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		Toast.makeText(context, result, Toast.LENGTH_LONG).show();
	}

}
