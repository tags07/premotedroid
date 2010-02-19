package org.pierre.remotedroid.client.app;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.pierre.remotedroid.client.R;
import org.pierre.remotedroid.client.activity.HelpActivity;
import org.pierre.remotedroid.protocol.PRemoteDroidActionReceiver;
import org.pierre.remotedroid.protocol.PRemoteDroidConnection;
import org.pierre.remotedroid.protocol.action.AuthentificationAction;
import org.pierre.remotedroid.protocol.action.PRemoteDroidAction;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PRemoteDroid extends Application implements Runnable, PRemoteDroidActionReceiver
{
	private SharedPreferences preferences;
	private Vibrator vibrator;
	
	private PRemoteDroidConnection connection;
	
	private ArrayList<PRemoteDroidActionReceiver> actionReceiverList;
	
	private Handler handler;
	
	public void onCreate()
	{
		super.onCreate();
		
		this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.settings, true);
		
		this.vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		
		if (this.preferences.getBoolean("debug_firstRun", true))
		{
			Intent intent = new Intent(this, HelpActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(intent);
			
			Editor editor = this.preferences.edit();
			editor.putBoolean("debug_firstRun", false);
			editor.commit();
		}
		
		this.actionReceiverList = new ArrayList<PRemoteDroidActionReceiver>();
		
		this.handler = new Handler();
	}
	
	public SharedPreferences getPreferences()
	{
		return this.preferences;
	}
	
	public void vibrate(long l)
	{
		this.vibrator.vibrate(l);
	}
	
	public synchronized void run()
	{
		try
		{
			String server = this.preferences.getString("connection_server", null);
			int port = Integer.parseInt(this.preferences.getString("connection_port", null));
			String password = this.preferences.getString("connection_password", null);
			
			this.connection = new PRemoteDroidConnection(new Socket(server, port));
			
			this.showToast(R.string.text_connection_established);
			
			try
			{
				this.sendAction(new AuthentificationAction(password));
				
				while (true)
				{
					PRemoteDroidAction action = this.connection.receiveAction();
					
					this.receiveAction(action);
				}
			}
			finally
			{
				this.connection.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			if (this.connection != null)
			{
				this.showToast(R.string.text_connection_closed);
			}
			else
			{
				this.showToast(R.string.text_connection_refused);
			}
			
			this.connection = null;
		}
	}
	
	public void sendAction(PRemoteDroidAction action)
	{
		if (this.connection != null)
		{
			try
			{
				this.connection.sendAction(action);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void showToast(final int resId)
	{
		this.handler.post(new Runnable()
		{
			public void run()
			{
				Toast.makeText(PRemoteDroid.this, resId, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public void receiveAction(PRemoteDroidAction action)
	{
		synchronized (this.actionReceiverList)
		{
			for (PRemoteDroidActionReceiver actionReceiver : this.actionReceiverList)
			{
				actionReceiver.receiveAction(action);
			}
		}
	}
	
	public void registerActionReceiver(PRemoteDroidActionReceiver actionReceiver)
	{
		synchronized (this.actionReceiverList)
		{
			this.actionReceiverList.add(actionReceiver);
			
			if (this.actionReceiverList.size() > 0)
			{
				this.startConnection();
			}
		}
	}
	
	public void unregisterActionReceiver(PRemoteDroidActionReceiver actionReceiver)
	{
		synchronized (this.actionReceiverList)
		{
			this.actionReceiverList.remove(actionReceiver);
			
			if (this.actionReceiverList.size() == 0)
			{
				this.stopConnection();
			}
		}
	}
	
	public void startConnection()
	{
		(new Thread(this)).start();
		System.out.println("Start connection");
	}
	
	public void stopConnection()
	{
		System.out.println("stop connection");
		
		if (this.connection != null)
		{
			try
			{
				this.connection.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
