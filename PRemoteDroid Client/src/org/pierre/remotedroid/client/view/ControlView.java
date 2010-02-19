package org.pierre.remotedroid.client.view;

import org.pierre.remotedroid.client.activity.ControlActivity;
import org.pierre.remotedroid.client.app.PRemoteDroid;
import org.pierre.remotedroid.client.control.ControlType;
import org.pierre.remotedroid.protocol.PRemoteDroidActionReceiver;
import org.pierre.remotedroid.protocol.action.PRemoteDroidAction;
import org.pierre.remotedroid.protocol.action.ScreenCaptureRequestAction;
import org.pierre.remotedroid.protocol.action.ScreenCaptureResponseAction;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ControlView extends ImageView implements Runnable, PRemoteDroidActionReceiver
{
	private PRemoteDroid application;
	private ControlActivity controlActivity;
	private SharedPreferences preferences;
	
	private ControlType controlType;
	
	private Bitmap currentBitmap;
	private Bitmap newBitmap;
	
	private boolean screenCaptureEnabled;
	private byte screenCaptureFormat;
	
	private Paint paint;
	private float screenDensity;
	
	public ControlView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.controlActivity = (ControlActivity) context;
		
		this.application = (PRemoteDroid) this.controlActivity.getApplication();
		
		this.preferences = application.getPreferences();
		
		this.paint = new Paint();
		this.paint.setColor(Color.BLACK);
		this.paint.setAntiAlias(true);
		
		this.screenDensity = this.getResources().getDisplayMetrics().density;
	}
	
	public synchronized void receiveAction(PRemoteDroidAction action)
	{
		if (action instanceof ScreenCaptureResponseAction)
		{
			ScreenCaptureResponseAction scra = (ScreenCaptureResponseAction) action;
			
			if (this.newBitmap != null)
			{
				this.newBitmap.recycle();
			}
			
			this.newBitmap = BitmapFactory.decodeByteArray(scra.data, 0, scra.data.length);
			
			this.post(this);
		}
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
	
	protected void onWindowVisibilityChanged(int visibility)
	{
		super.onWindowVisibilityChanged(visibility);
		
		if (visibility == VISIBLE)
		{
			this.application.registerActionReceiver(this);
			
			this.controlType = ControlType.getControl(this.controlActivity, this.preferences.getString("control_type", null));
			
			this.screenCaptureEnabled = this.preferences.getBoolean("screenCapture_enabled", false);
			
			String format = this.preferences.getString("screenCapture_format", null);
			if (format.equals("png"))
			{
				this.screenCaptureFormat = ScreenCaptureRequestAction.FORMAT_PNG;
			}
			else if (format.equals("jpg"))
			{
				this.screenCaptureFormat = ScreenCaptureRequestAction.FORMAT_JPG;
			}
		}
		else
		{
			this.application.unregisterActionReceiver(this);
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
				this.application.sendAction(new ScreenCaptureRequestAction((short) width, (short) height, this.screenCaptureFormat));
			}
		}
	}
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if (this.screenCaptureEnabled)
		{
			canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, 5 * this.screenDensity, this.paint);
		}
	}
}
