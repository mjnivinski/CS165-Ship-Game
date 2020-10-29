package a3.myGameEngine.flightControls;

import a3.myGameEngine.SimpleMath;
import ray.rage.Engine;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class ShipController {

	private Engine eng;
	private FlightController FC;
	private SceneNode ship;
	
	private float pitchRate = 150f;
	private float pitchAccel = 5f;
	private float rollRate = 150f;
	private float rollAccel = 5f;
	private float yawRate = 15f;
	private float shipSpeed = 10f;
	
	
	private float pitch, leftVertical, pitchForward, pitchBackward;
	private float roll, leftHorizontal, rollLeft, rollRight;
	
	private float rightHorizontal, rightVertical;
	
	private float yaw, leftBumper, rightBumper, yawLeft, yawRight;
	
	private float throttle, throttleUp, throttleDown;
	
	public ShipController(Engine e, FlightController f, SceneNode ship) {
		eng = e;
		FC = f;
		this.ship = ship;
	}
	
	float offset = 2;
	float smoothSpeed = 10f;
	
	float deltaTime;
	public void update() {
		deltaTime = eng.getElapsedTimeMillis()/1000;
		//this section is basically logic for a ship simulator.
		
		//TEMPORARY
		//it displays the current usage of the controller
		
		/*
		System.out.println("leftHorizontal: " + leftHorizontal + " leftVertical: " + leftVertical
				+ "\nrightHorizontal: " + rightHorizontal + " rightVertical: " + rightVertical
				+ "\nrollLeft: " + rollLeft + " rollRight: " + rollRight
				+ "\npitchForward: " + pitchForward + " pitchBackward: " + pitchBackward
				);
		*/
		
		//System.out.println("leftVertical: " + leftVertical);
		//System.out.println("keyValue: " + (pitchForward - pitchBackward));
		
		//smoth movement
		/*
		Vector3f position = (Vector3f)ship.getLocalPosition();
		
		float currentY = position.y();
		//offset + (width * e.getValue());
		
		float destinationY = offset + leftVertical;
		
		float smoothDestination = SimpleMath.lerp(currentY, destinationY, smoothSpeed * deltaTime);
		
		ship.setLocalPosition(position.x(),smoothDestination,position.z());
		*/
		
		pitch();
		roll();
		yaw();
		throttle();
	}
	
	private void pitch() {
		//System.out.println("leftVertical: " + leftVertical);
		float value;
		float keyValue = pitchForward - pitchBackward;
		
		
		
		if(keyValue > 0) {
			if(leftVertical > 0) value = 1;
			else value = keyValue + leftVertical;
		}
		else if(keyValue < 0) {
			if(leftVertical <= 0) value = -1;
			else value = keyValue + leftVertical;
		}
		else {
			value = leftVertical;
		}
		
		if(value < 0) {
			value *= value;
			value *= -1;
		}
		else value *= value;
		
		
		pitch = SimpleMath.lerp(pitch, value, pitchAccel * deltaTime);
		
		ship.pitch(Degreef.createFrom(pitchRate * pitch * deltaTime));
		
	}
	
	private void roll() {
		
		float value;
		float keyValue = rollLeft - rollRight;
		
		if(keyValue > 0) {
			if(leftHorizontal > 0) value = 1;
			else value = keyValue + leftHorizontal;
		}
		else if(keyValue < 0) {
			if(leftHorizontal <= 0) value = -1;
			else value = keyValue + leftHorizontal;
		}
		else {
			value = leftHorizontal;
		}
		
		if(value < 0) {
			value *= value;
			value *= -1;
		}
		else value *= value;
		
		
		roll = SimpleMath.lerp(roll, value, rollAccel * deltaTime);
		
		//System.out.println("LERP: " + SimpleMath.lerp(10, 5, 0.5f));
		
		//System.out.println("" + deltaTime * pitchRate)
		//System.out.println("pitch: " + pitch);
		
		//System.out.println("pitch: " + value + " keyValue: " + keyValue + "  pitchForward: " + pitchForward + " - pitchBackward" + pitchBackward + "    " + leftVertical);
		
		//System.out.println("pitch: " + pitch);
		
		ship.roll(Degreef.createFrom(rollRate * roll * deltaTime));
	}
	
	private void yaw() {
		
		float value;
		float keyValue = yawLeft - yawRight;
		float bumperValue = leftBumper - rightBumper;
		
		value = keyValue + bumperValue;
		
		if(value > 0) value = 1;
		else if(value < 0) value = -1;
		else value = 0;
		
		
		//POSITIVE IS LEFT
		
		ship.yaw(Degreef.createFrom(yawRate * value * deltaTime));
		
		//float value = yawLeft - yawRight;
		
		//ship.yaw(Degreef.createFrom(yawRate * value * deltaTime));
	}
	
	private void throttle() {
		float value;
		float keyValue = throttleUp - throttleDown;
		
		if(keyValue > 0) {
			if(throttle > 0) value = 1;
			else value = keyValue + throttle;
		}
		else if(keyValue < 0) {
			if(throttle <= 0) value = -1;
			else value = keyValue + throttle;
		}
		else {
			value = throttle;
		}
		
		
		ship.moveForward(shipSpeed * value * deltaTime);
	}
	
	public void setLeftHorizontal(float v) { leftHorizontal = v;}
	public void setLeftVertical(float v) { leftVertical = v;}
	public void setRightHorizontal(float v) { rightHorizontal = v;}
	public void setRightVertical(float v) { rightVertical = v;}
	public void setLeftBumper(float v) { leftBumper = v; }
	public void setRightBumper(float v) { rightBumper = v; }
	public void setThrottle(float v) { throttle = v; }
	
	
	public void setRollLeft(float v) { rollLeft = v;}
	public void setRollRight(float v) { rollRight = v;}
	public void setPitchFoward(float v) { pitchForward = v;}
	public void setPitchBackward(float v) { pitchBackward = v;}
	public void setYawLeft(float v) { yawLeft = v; }
	public void setYawRight(float v) { yawRight = v; }
	public void setThrottleUp(float v) { throttleUp = v; }
	public void setThrottleDown(float v) { throttleDown = v; }
}
