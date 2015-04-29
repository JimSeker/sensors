package edu.cs4730.input;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//there is no code to see here.  Just the activity, which calls the fragment.

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new MainFragment()).commit();
		}
	}
}
