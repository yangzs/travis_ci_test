package com.yac.yacapp.test;

import com.yac.yacapp.activities.VerifyActivity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

public class MainActivityTest extends ActivityInstrumentationTestCase2<VerifyActivity> {

	Activity mainActivity = null;
	Button button = null;

	public MainActivityTest() {
		super(VerifyActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mainActivity = getActivity();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testChangeText() throws Exception{
		System.out.println("MainActivityTest  testChangeText is run.");
		assertTrue(true);
	}
}
