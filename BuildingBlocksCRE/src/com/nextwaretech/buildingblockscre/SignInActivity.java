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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.nextwaretech.buildingblockscre.R;

public class SignInActivity extends Activity implements OnClickListener {

	private final String URI = Data.SERVER_NAME+"users/sign_in.json";
	private final String FILE_NAME = "credentials.txt";
	EditText email;
	EditText password;
	CheckBox keepLoggedIn;
	Button signinBt;
	FileManager fileman;
	boolean checked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in);

		fileman = new FileManager(this);

		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		keepLoggedIn = (CheckBox) findViewById(R.id.keep_logged_in);
		signinBt = (Button) findViewById(R.id.signin_bt);
		signinBt.setOnClickListener((OnClickListener) this);
		checked = false;

		String data = "";
		if (fileman.fileExists(FILE_NAME)) {
			try {
				data = fileman.readFile(FILE_NAME);
				JSONObject json = new JSONObject(data);
				JSONObject cred = json.getJSONObject("credentials");
				email.setText(cred.getString("email"));
				if (cred.has("password")) {
					password.setText(cred.getString("password"));
					keepLoggedIn.setChecked(true);
					checked = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// Testing purpose
		// email.setText("shawn@nextwaretech.com");
		// password.setText("shawn@nextwaretech.com");
	}

	@Override
	public void onClick(View v) {
		// write user name to file and add password if remember this account is
		// checked
		if (!fileman.fileExists(FILE_NAME)
				|| (checked && !keepLoggedIn.isChecked())
				|| (!checked && keepLoggedIn.isChecked())) {
			StringBuilder credentials = new StringBuilder();
			credentials.append("{\n\t\"credentials\":{\n\t\t\"email\":");
			credentials.append("\"" + email.getText().toString() + "\"");
			if (keepLoggedIn.isChecked()) {
				credentials.append(",\n\t\t\"password\":");
				credentials.append("\"" + password.getText().toString() + "\"");
			}
			credentials.append("\n\t}\n}");
			Log.v("damn", credentials.toString());

			try {
				fileman.writeToFile(credentials.toString(), FILE_NAME);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		SignInNetworkTask signInTask = new SignInNetworkTask();
		signInTask.execute(URI, email.getText().toString(), password.getText().toString());
	}

	@Override
	public void onBackPressed() {
		final Dialog DIALOG = new Dialog(this);
		DIALOG.setContentView(R.layout.exit_dialog);
		DIALOG.show();

		Button exit = (Button) DIALOG.findViewById(R.id.exit);
		exit.setText("Exit");

		Button cancel = (Button) DIALOG.findViewById(R.id.cancel_exit);
		cancel.setText("Cancel");

		exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DIALOG.dismiss();
				SignInActivity.this.finish();
				/*Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);*/
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DIALOG.dismiss();
			}
		});

	}

	private class SignInNetworkTask extends AsyncTask<String, Integer, String> {

		public SignInNetworkTask() {
		}

		@Override
		protected String doInBackground(String... strs) {
			String uri = strs[0];
			String email = strs[1];
			String password = strs[2];

			StringBuilder builder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			JSONObject parentData = new JSONObject();
			JSONObject childData = new JSONObject();
			JSONObject jsonResponse;
			JSONObject session;
			HttpPost httpPost;

			try {
				httpPost = new HttpPost(uri);

				childData.put("email", email);
				childData.put("password", password);
				childData.put("deviceid", "132132");
				parentData.put("user", childData);

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
				} else {
					Log.e(AllContactsActivity.class.toString(),
							"Failed to fetch contacts");
				}

				jsonResponse = new JSONObject(builder.toString());
				session = jsonResponse.getJSONObject("session");

				Log.v("signinresp",
						session.getString("error") + " "
								+ session.getString("auth_token") + " "
								+ session.getString("username"));

				if (session.getString("error").equals("Success")) {
					//Intent intent = new Intent("com.nextwaretech.buildingblockscre.AllContactsActivity");
					//Intent intent = new Intent("com.nextwaretech.buildingblockscre.IncomingCallActivity");
					Intent intent = new Intent("com.nextwaretech.buildingblockscre.AllPropertiesActivity");
					// intent.putExtra("auth_token",
					// session.getString("auth_token"));
					Data.authToken = session.getString("auth_token");
					Data.userName = session.getString("username");
					startActivity(intent);
				} else {
					Log.v("signinresp", "Sign in unsuccessful");
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

			return null;
		}

	}

}
