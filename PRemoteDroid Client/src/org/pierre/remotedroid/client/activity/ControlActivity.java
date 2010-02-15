package org.pierre.remotedroid.client.activity;

import java.io.IOException;
import java.net.Socket;

import org.pierre.remotedroid.client.R;
import org.pierre.remotedroid.client.app.PRemoteDroid;
import org.pierre.remotedroid.client.view.ControlView;
import org.pierre.remotedroid.protocol.PRemoteDroidConnection;
import org.pierre.remotedroid.protocol.action.AuthentificationAction;
import org.pierre.remotedroid.protocol.action.AuthentificationResponseAction;
import org.pierre.remotedroid.protocol.action.MouseClickAction;
import org.pierre.remotedroid.protocol.action.MouseMoveAction;
import org.pierre.remotedroid.protocol.action.MouseWheelAction;
import org.pierre.remotedroid.protocol.action.PRemoteDroidAction;
import org.pierre.remotedroid.protocol.action.ScreenCaptureResponseAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class ControlActivity extends Activity implements Runnable
{
	private static final int keyboardMenuItemId = 0;
	private static final int settingsMenuItemId = 1;
	private static final int getServerMenuItemId = 2;
	private static final int helpMenuItemId = 3;
	
	private PRemoteDroidConnection connection;
	private ControlView controlView;
	private Vibrator vibrator;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			this.setContentView(R.layout.control_landscape);
		}
		else
		{
			this.setContentView(R.layout.control_portrait);
		}
		
		this.controlView = (ControlView) this.findViewById(R.id.controlView);
		
		this.vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	protected void onResume()
	{
		super.onResume();
		
		(new Thread(this)).start();
	}
	
	protected void onPause()
	{
		super.onPause();
		
		if (this.connection != null)
		{
			try
			{
				this.connection.close();
			}
			catch (IOException e)
			{
			}
		}
	}
	
	public boolean onTrackballEvent(MotionEvent event)
	{
		return this.controlView.onTrackballEvent(event);
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, keyboardMenuItemId, Menu.NONE, this.getResources().getString(R.string.text_keyboard));
		menu.add(Menu.NONE, settingsMenuItemId, Menu.NONE, this.getResources().getString(R.string.text_settings));
		menu.add(Menu.NONE, getServerMenuItemId, Menu.NONE, this.getResources().getString(R.string.text_get_server));
		menu.add(Menu.NONE, helpMenuItemId, Menu.NONE, this.getResources().getString(R.string.text_help));
		
		return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case keyboardMenuItemId:
				this.toggleKeyboard();
				this.showToast(R.string.text_keyboard_not_supported);
				break;
			case settingsMenuItemId:
				this.startActivity(new Intent(this, SettingsActivity.class));
				break;
			case getServerMenuItemId:
				this.startActivity(new Intent(this, GetServerActivity.class));
				break;
			case helpMenuItemId:
				this.startActivity(new Intent(this, HelpActivity.class));
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void run()
	{
		try
		{
			SharedPreferences preferences = ((PRemoteDroid) this.getApplication()).getPreferences();
			
			String server = preferences.getString("connection_server", null);
			int port = Integer.parseInt(preferences.getString("connection_port", null));
			this.connection = new PRemoteDroidConnection(new Socket(server, port));
			
			this.showToast(R.string.text_connection_established);
			
			try
			{
				String password = preferences.getString("connection_password", null);
				this.sendAction(new AuthentificationAction(password));
				
				this.controlView.screenCaptureRequest();
				
				while (true)
				{
					PRemoteDroidAction action = this.connection.receiveAction();
					
					if (action != null)
					{
						this.action(action);
					}
				}
			}
			finally
			{
				this.connection.close();
			}
		}
		catch (IOException e)
		{
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
	
	private void action(PRemoteDroidAction action)
	{
		if (action instanceof ScreenCaptureResponseAction)
		{
			this.controlView.action((ScreenCaptureResponseAction) action);
		}
		else if (action instanceof AuthentificationResponseAction)
		{
			this.authentificateResponse((AuthentificationResponseAction) action);
		}
	}
	
	private void authentificateResponse(AuthentificationResponseAction action)
	{
		if (action.authentificated)
		{
			this.showToast(R.string.text_authentificated);
		}
		else
		{
			this.showToast(R.string.text_not_authentificated);
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
	
	public void mouseMove(int moveX, int moveY)
	{
		MouseMoveAction action = new MouseMoveAction((short) moveX, (short) moveY);
		this.sendAction(action);
	}
	
	public void mouseClick(byte button, boolean state)
	{
		MouseClickAction action = new MouseClickAction(button, state);
		this.sendAction(action);
	}
	
	public void mouseWheel(int amount)
	{
		MouseWheelAction action = new MouseWheelAction((byte) amount);
		this.sendAction(action);
	}
	
	public void vibrate(long l)
	{
		this.vibrator.vibrate(l);
	}
	
	private void showToast(final int resId)
	{
		this.controlView.post(new Runnable()
		{
			public void run()
			{
				Toast.makeText(ControlActivity.this, resId, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void toggleKeyboard()
	{
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, 0);
	}
}
