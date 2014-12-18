package edu.cs4730.input2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * nothing interesting here.  Just a fragment.
 * Everything for this example is in the MainActivity.
 * all the overridden listeners are there, they could just as easily be here instead, but 
 * I showing how to override for the "screen".
 * 
 */
public class MainFragment extends Fragment {

	public MainFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

}
