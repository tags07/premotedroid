package org.pierre.remotedroid.client.control;

import org.pierre.remotedroid.client.R;
import org.pierre.remotedroid.client.activity.ControlActivity;
import org.pierre.remotedroid.client.app.PRemoteDroid;
import org.pierre.remotedroid.client.view.ClickView;
import org.pierre.remotedroid.protocol.action.MouseClickAction;

import android.content.SharedPreferences;
import android.view.MotionEvent;

public abstract class ControlType
{
	protected ControlActivity controlActivity;
	protected SharedPreferences preferences;
	
	private float downX;
	private float downY;
	private boolean holdPossible;
	
	private long clickDelay;
	private long holdDelay;
	private float immobileDistance;
	
	private ClickView leftClickView;
	
	protected ControlType(ControlActivity controlActivity)
	{
		this.controlActivity = controlActivity;
		
		this.preferences = ((PRemoteDroid) this.controlActivity.getApplication()).getPreferences();
		
		this.clickDelay = Long.parseLong(this.preferences.getString("control_click_delay", null));
		this.holdDelay = Long.parseLong(this.preferences.getString("control_hold_delay", null));
		this.immobileDistance = Float.parseFloat(this.preferences.getString("control_immobile_distance", null));
		this.immobileDistance *= this.controlActivity.getResources().getDisplayMetrics().density;
		
		this.leftClickView = (ClickView) this.controlActivity.findViewById(R.id.leftClickView);
	}
	
	public final static ControlType getControl(ControlActivity controlActivity, String type)
	{
		if (type.equals("trackpad"))
		{
			return new TrackpadControl(controlActivity);
		}
		else if (type.equals("touchpad"))
		{
			return new TouchpadControl(controlActivity);
		}
		else
		{
			return null;
		}
	}
	
	public void onTouchEvent(MotionEvent event)
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
	}
	
	protected void onTouchDown(MotionEvent event)
	{
		this.downX = event.getRawX();
		this.downY = event.getRawY();
		
		this.holdPossible = true;
	}
	
	protected void onTouchMove(MotionEvent event)
	{
		if (this.holdPossible)
		{
			if (this.getDistanceFromDown(event) > this.immobileDistance)
			{
				this.holdPossible = false;
			}
			else if (event.getEventTime() - event.getDownTime() > this.holdDelay)
			{
				this.controlActivity.mouseClick(MouseClickAction.BUTTON_LEFT, MouseClickAction.STATE_DOWN);
				
				this.holdPossible = false;
				
				this.leftClickView.setPressed(true);
				this.leftClickView.setHold(true);
				
				this.controlActivity.vibrate(100);
			}
		}
	}
	
	protected void onTouchUp(MotionEvent event)
	{
		if (event.getEventTime() - event.getDownTime() < this.clickDelay && this.getDistanceFromDown(event) <= this.immobileDistance)
		{
			if (this.leftClickView.isPressed())
			{
				this.controlActivity.vibrate(100);
			}
			else
			{
				this.controlActivity.mouseClick(MouseClickAction.BUTTON_LEFT, MouseClickAction.STATE_DOWN);
				
				this.controlActivity.vibrate(50);
			}
			
			this.controlActivity.mouseClick(MouseClickAction.BUTTON_LEFT, MouseClickAction.STATE_UP);
			
			this.leftClickView.setPressed(false);
			this.leftClickView.setHold(false);
		}
	}
	
	public void onTrackballEvent(MotionEvent event)
	{
		int amount = Math.round(event.getY() * 6);
		
		if (amount != 0)
		{
			this.controlActivity.mouseWheel(amount);
		}
	}
	
	private double getDistanceFromDown(MotionEvent event)
	{
		return Math.sqrt(Math.pow(event.getRawX() - this.downX, 2) + Math.pow(event.getRawY() - this.downY, 2));
	}
}