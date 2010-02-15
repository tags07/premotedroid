package org.pierre.remotedroid.client.control;

import org.pierre.remotedroid.client.activity.ControlActivity;
import org.pierre.remotedroid.client.app.PRemoteDroid;

import android.view.MotionEvent;

public class TrackpadControl extends ControlType
{
	private float sensitivity;
	private float acceleration;
	
	private float originX;
	private float originY;
	private float resultX;
	private float resultY;
	
	public TrackpadControl(ControlActivity controlActivity)
	{
		super(controlActivity);
		
		this.sensitivity = Float.parseFloat(PRemoteDroid.preferences().getString("control_trackpad_sensitivity", null));
		this.sensitivity /= this.controlActivity.getResources().getDisplayMetrics().density;
		this.acceleration = Float.parseFloat(PRemoteDroid.preferences().getString("control_trackpad_acceleration", null));
	}
	
	protected void onTouchMove(MotionEvent event)
	{
		super.onTouchMove(event);
		
		float moveXRaw = (event.getX() - this.originX) * this.sensitivity;
		float moveYRaw = (event.getY() - this.originY) * this.sensitivity;
		
		moveXRaw = (float) ((Math.pow(Math.abs(moveXRaw), this.acceleration) * Math.signum(moveXRaw)) + this.resultX);
		moveYRaw = (float) ((Math.pow(Math.abs(moveYRaw), this.acceleration) * Math.signum(moveYRaw)) + this.resultY);
		
		int moveXFinal = Math.round(moveXRaw);
		int moveYFinal = Math.round(moveYRaw);
		
		this.resultX = moveXRaw - moveXFinal;
		this.resultY = moveYRaw - moveYFinal;
		
		if (moveXFinal != 0 || moveYFinal != 0)
		{
			this.controlActivity.mouseMove(moveXFinal, moveYFinal);
		}
	}
	
	protected void onTouchDown(MotionEvent event)
	{
		super.onTouchDown(event);
		
		this.originX = event.getX();
		this.originY = event.getY();
		
		this.resultX = 0;
		this.resultY = 0;
	}
}
