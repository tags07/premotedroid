package org.pierre.remotedroid.client.app;

import org.pierre.remotedroid.client.R;
import org.pierre.remotedroid.client.activity.HelpActivity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PRemoteDroid extends Application
{
	private SharedPreferences preferences;
	
	public void onCreate()
	{
		super.onCreate();
		
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
		
		if (this.preferences.getBoolean("debug_firstRun", true))
		{
			Intent intent = new Intent(this, HelpActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
			
			Editor editor = this.preferences.edit();
			editor.putBoolean("debug_firstRun", false);
			editor.commit();
		}
	}
	
	public SharedPreferences getPreferences()
	{
		return this.preferences;
	}
}
