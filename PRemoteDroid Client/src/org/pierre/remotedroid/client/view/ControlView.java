package org.pierre.remotedroid.client.view;

import org.pierre.remotedroid.client.activity.ControlActivity;
import org.pierre.remotedroid.client.app.PRemoteDroid;
import org.pierre.remotedroid.client.control.ControlType;
import org.pierre.remotedroid.protocol.action.ScreenCaptureRequestAction;
import org.pierre.remotedroid.protocol.action.ScreenCaptureResponseAction;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ControlView extends ImageView implements Runnable
{
	private ControlActivity controlActivity;
	
	private ControlType controlType;
	
	private Bitmap currentBitmap;
	private Bitmap newBitmap;
	
	private boolean screenCaptureEnabled;
	private byte screenCaptureFormat;
	
	public ControlView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.controlActivity = (ControlActivity) context;
	}
	
	public synchronized void action(ScreenCaptureResponseAction action)
	{
		if (this.newBitmap != null)
		{
			this.newBitmap.recycle();
		}
		
		this.newBitmap = BitmapFactory.decodeByteArray(action.data, 0, action.data.length);
		
		this.post(this);
	}
	
	public synchronized void run()
	{
		if (this.currentBitmap != null)
		{
			this.currentBitmap.recycle();
		}
		
		this.currentBitmap = this.newBitmap;
		this.newBitmap = null;
		
		this.setImageBitmap(this.currentBitmap);
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		this.controlType.onTouchEvent(event);
		
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			this.screenCaptureRequest();
		}
		
		event.recycle();
		
		return true;
	}
	
	public boolean onTrackballEvent(MotionEvent event)
	{
		this.controlType.onTrackballEvent(event);
		
		event.recycle();
		
		return true;
	}
	
	protected void onWindowVisibilityChanged(int visibility)
	{
		super.onWindowVisibilityChanged(visibility);
		
		if (visibility == VISIBLE)
		{
			SharedPreferences preferences = ((PRemoteDroid) this.controlActivity.getApplication()).getPreferences();
			
			this.controlType = ControlType.getControl(this.controlActivity, preferences.getString("control_type", "trackpad"));
			
			this.screenCaptureEnabled = preferences.getBoolean("screenCapture_enabled", false);
			
			String format = preferences.getString("screenCapture_format", null);
			if (format.equals("png"))
			{
				this.screenCaptureFormat = ScreenCaptureRequestAction.FORMAT_PNG;
			}
			else if (format.equals("jpg"))
			{
				this.screenCaptureFormat = ScreenCaptureRequestAction.FORMAT_JPG;
			}
		}
	}
	
	protected synchronized void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		
		if (this.currentBitmap != null)
		{
			this.currentBitmap.recycle();
		}
	}
	
	public void screenCaptureRequest()
	{
		if (this.screenCaptureEnabled)
		{
			int width = this.getWidth();
			int height = this.getHeight();
			
			if (width != 0 && height != 0)
			{
				this.controlActivity.sendAction(new ScreenCaptureRequestAction((short) width, (short) height, this.screenCaptureFormat));
			}
		}
	}
}
