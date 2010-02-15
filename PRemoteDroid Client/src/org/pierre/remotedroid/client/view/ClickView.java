package org.pierre.remotedroid.client.view;

import org.pierre.remotedroid.client.R;
import org.pierre.remotedroid.client.activity.ControlActivity;
import org.pierre.remotedroid.client.app.PRemoteDroid;
import org.pierre.remotedroid.protocol.action.MouseClickAction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ClickView extends View
{
	private static Paint paint;
	
	private ControlActivity controlActivity;
	
	private byte button;
	private boolean state;
	private boolean hold;
	private long holdDelay;
	
	static
	{
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(8);
	}
	
	public ClickView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.controlActivity = (ControlActivity) context;
		
		switch (this.getId())
		{
			case R.id.leftClickView:
				this.button = MouseClickAction.BUTTON_LEFT;
				break;
			case R.id.middleClickView:
				this.button = MouseClickAction.BUTTON_MIDDLE;
				break;
			case R.id.rightClickView:
				this.button = MouseClickAction.BUTTON_RIGHT;
				break;
			default:
				this.button = MouseClickAction.BUTTON_NONE;
				break;
		}
		
		this.state = MouseClickAction.STATE_UP;
		
		this.hold = false;
		
		this.holdDelay = Long.parseLong(PRemoteDroid.preferences().getString("control_hold_delay", null));
	}
	
	public boolean getState()
	{
		return state;
	}
	
	public void setState(boolean state)
	{
		this.state = state;
	}
	
	public boolean isHold()
	{
		return hold;
	}
	
	public void setHold(boolean hold)
	{
		this.hold = hold;
	}
	
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		
		if (this.state == MouseClickAction.STATE_UP)
		{
			canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), paint);
		}
		else if (this.state == MouseClickAction.STATE_DOWN)
		{
			canvas.drawPaint(paint);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_MOVE:
			{
				this.onTouchMove(event);
				break;
			}
				
			case MotionEvent.ACTION_DOWN:
			{
				this.onTouchDown(event);
				break;
			}
				
			case MotionEvent.ACTION_UP:
			{
				this.onTouchUp(event);
				break;
			}
				
			default:
				break;
		}
		
		event.recycle();
		
		return true;
	}
	
	private void onTouchDown(MotionEvent event)
	{
		if (!this.hold)
		{
			this.controlActivity.mouseClick(this.button, MouseClickAction.STATE_DOWN);
			
			this.state = MouseClickAction.STATE_DOWN;
			
			this.postInvalidate();
			
			this.controlActivity.vibrate(50);
		}
		else
		{
			this.hold = false;
		}
	}
	
	private void onTouchMove(MotionEvent event)
	{
		if (!this.hold && event.getEventTime() - event.getDownTime() >= this.holdDelay)
		{
			this.hold = true;
			
			this.controlActivity.vibrate(100);
		}
	}
	
	private void onTouchUp(MotionEvent event)
	{
		if (!this.hold)
		{
			this.controlActivity.mouseClick(this.button, MouseClickAction.STATE_UP);
			
			this.state = MouseClickAction.STATE_UP;
			
			this.postInvalidate();
		}
	}
}