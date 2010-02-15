package org.pierre.remotedroid.client.app;

import org.pierre.remotedroid.client.R;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PRemoteDroid extends Application
{
	private static SharedPreferences preferences;
	
	public void onCreate()
	{
		super.onCreate();
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
	}
	
	public static SharedPreferences preferences()
	{
		return preferences;
	}
}
