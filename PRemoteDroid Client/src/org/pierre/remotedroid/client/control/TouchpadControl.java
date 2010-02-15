package org.pierre.remotedroid.client.control;

import org.pierre.remotedroid.client.activity.ControlActivity;

import android.view.MotionEvent;

public class TouchpadControl extends ControlType
{
	private float sensitivity;
	private float acceleration;
	
	private float previousX;
	private float previousY;
	private float resultX;
	private float resultY;
	
	public TouchpadControl(ControlActivity controlActivity)
	{
		super(controlActivity);
		
		this.sensitivity = Float.parseFloat(this.preferences.getString("control_touchpad_sensitivity", null));
		this.sensitivity /= this.controlActivity.getResources().getDisplayMetrics().density;
		this.acceleration = Float.parseFloat(this.preferences.getString("control_touchpad_acceleration", null));
	}
	
	protected void onTouchDown(MotionEvent event)
	{
		super.onTouchDown(event);
		
		this.previousX = event.getRawX();
		this.previousY = event.getRawY();
		
		this.resultX = 0;
		this.resultY = 0;
	}
	
	protected void onTouchMove(MotionEvent event)
	{
		super.onTouchMove(event);
		
		float moveXRaw = event.getRawX() - this.previousX;
		float moveYRaw = event.getRawY() - this.previousY;
		
		moveXRaw *= this.sensitivity;
		moveYRaw *= this.sensitivity;
		
		moveXRaw = (float) ((Math.pow(Math.abs(moveXRaw), this.acceleration) * Math.signum(moveXRaw)));
		moveYRaw = (float) ((Math.pow(Math.abs(moveYRaw), this.acceleration) * Math.signum(moveYRaw)));
		
		moveXRaw += this.resultX;
		moveYRaw += this.resultY;
		
		int moveXFinal = Math.round(moveXRaw);
		int moveYFinal = Math.round(moveYRaw);
		
		this.resultX = moveXRaw - moveXFinal;
		this.resultY = moveYRaw - moveYFinal;
		
		if (moveXFinal != 0 || moveYFinal != 0)
		{
			this.controlActivity.mouseMove(moveXFinal, moveYFinal);
		}
		
		this.previousX = event.getRawX();
		this.previousY = event.getRawY();
	}
}
