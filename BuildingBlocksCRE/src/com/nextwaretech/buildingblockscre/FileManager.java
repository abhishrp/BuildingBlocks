package com.nextwaretech.buildingblockscre;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class FileManager {

	private Context context;

	public FileManager(Context c) {
		context = c;
	}

	// file operations
	public boolean fileExists(String fileName) {

		String[] files = context.fileList();
		for (String file : files) {
			if (file.equals(fileName)) {
				//Toast.makeText(context, "NOT First Time", Toast.LENGTH_LONG).show();
				return true;
			}
		}
		return false;
	}

	public void writeToFile(String data, String fileName)
			throws IOException {
		// Write to file
		// Create blank file if does not exist
		try {
			FileOutputStream fos;
			//fos = context.openFileOutput(fileName, Context.MODE_APPEND);
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			fos.close();
		} catch (IOException e) {
			throw e;
		}
	}

	public String readFile(String filename)
			throws IOException {
		StringBuilder builder = new StringBuilder();
		try {
			FileInputStream in = context.openFileInput(filename);
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				Log.v("file", line);
				builder.append(line);
			}
			in.close();
		} catch (IOException e) {
			throw e;
		}

		return builder.toString();
	}

}
