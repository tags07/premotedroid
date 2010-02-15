package org.pierre.remotedroid.client.activity;

import org.pierre.remotedroid.client.R;
import org.pierre.remotedroid.client.app.PRemoteDroid;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity
{
	private static String[] tabFloatPreferences = { "control_trackpad_sensitivity", "control_trackpad_acceleration", "control_touchpad_sensitivity", "control_touchpad_acceleration", "control_immobile_distance" };
	private static String[] tabIntPreferences = { "connection_port", "control_click_delay", "control_hold_delay" };
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		this.addPreferencesFromResource(R.xml.settings);
	}
	
	protected void onPause()
	{
		super.onPause();
		
		SharedPreferences preferences = ((PRemoteDroid) this.getApplication()).getPreferences();
		Editor editor = preferences.edit();
		
		for (String s : tabFloatPreferences)
		{
			try
			{
				Float.parseFloat(preferences.getString(s, null));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				editor.remove(s);
			}
		}
		
		for (String s : tabIntPreferences)
		{
			try
			{
				Integer.parseInt(preferences.getString(s, null));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				editor.remove(s);
			}
		}
		
		editor.commit();
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
	}
}
