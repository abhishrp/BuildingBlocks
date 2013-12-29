package com.nextwaretech.buildingblockscre;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddNoteFragment  extends Fragment {
	private static View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.add_note_fragment, container, true);
		if(view!=null)
			System.out.println("add note frament inflated");
		else
			System.out.println("view is null");
		return view;
	}
}
