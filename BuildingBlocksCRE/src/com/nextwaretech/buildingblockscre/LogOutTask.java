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
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LogOutTask extends AsyncTask<String, Integer, String> {

	private final String URI_NO_AUTH_TOKEN = Data.SERVER_NAME+"users/sign_out.json?auth_token=";
	private JSONObject jsonResponse;
	private Context context;
	private ProgressDialog progressDiag;
	private boolean success;
	
	public LogOutTask(Context context) {
		this.context = context;
		progressDiag = new ProgressDialog(context);
		success = false;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDiag.setMessage("Logging out...");
		progressDiag.show();
	}
	
	@Override
	protected String doInBackground(String... strs) {
		StringBuilder uriRaw = new StringBuilder(URI_NO_AUTH_TOKEN);
		uriRaw.append(Data.authToken);
		String uri = uriRaw.toString();
		
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		
		HttpGet httpGet;

		try {
			httpGet = new HttpGet(uri);
			httpGet.setHeader("Content-type", "application/json");

			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				success = true;
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.v("logout",	"Failed to logout "+statusCode);
			}

			jsonResponse = new JSONObject(builder.toString());

			Log.v("logout",jsonResponse.getJSONArray("message").get(0).toString());

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
		if(success) {
			//Intent intent = new Intent("com.nextwaretech.buildingblockscre.SignInActivity");
			Intent intent = new Intent(context,SignInActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Data.authToken = null;
			Data.userName = null;
			context.startActivity(intent);
		}
		else {
			Toast.makeText(context, "Logout Unsuccessful", Toast.LENGTH_SHORT).show();
		}
		progressDiag.dismiss();
	}

}
