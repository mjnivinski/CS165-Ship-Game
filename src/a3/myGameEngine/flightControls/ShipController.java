package a3.myGameEngine.flightControls;

import a3.myGameEngine.SimpleMath;
import ray.rage.Engine;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.*;

public class ShipController {
	
	ScriptEngine jsEngine;
	File paramFile;
	long fileLastModifiedTime;
			
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
		
		setupJavascript();
		
		eng = e;
		FC = f;
		this.ship = ship;
		
		setupParams();
	}
	
	private void setupParams() {
		pitchRate = (float)(double)jsEngine.get("pitchRate");
		
		pitchRate = (float)(double)jsEngine.get("pitchRate");
		pitchAccel = (float)(double)jsEngine.get("pitchAccel");
		rollRate = (float)(double)jsEngine.get("rollRate");
		rollAccel = (float)(double)jsEngine.get("rollAccel");
		yawRate = (float)(double)jsEngine.get("yawRate");
		shipSpeed = (float)(double)jsEngine.get("shipSpeed");
	}
	
	private void setupJavascript() {
		ScriptEngineManager factory = new ScriptEngineManager();
		
		//get the JavaScript engine
		jsEngine = factory.getEngineByName("js");
		
		//setuoDefaultParams script
		//executeScript("scripts/defaultParams.js");
		paramFile = new File("scripts/defaultParams.js");
		//System.out.println("getName: " + paramFile.getName());
		executeScript("scripts/" + paramFile.getName());
	}
	
	private void executeScript(String scriptFileName) {
		executeScript(jsEngine, scriptFileName);
	}
	
	private void executeScript(ScriptEngine engine, String scriptFileName) {
		try {
			FileReader fileReader = new FileReader(scriptFileName);
			engine.eval(fileReader); //execute the script statements in the file
			fileReader.close();
		}
		catch(FileNotFoundException e1) {
			System.out.println(scriptFileName + " not found " + e1);
		}
		catch(IOException e2) {
			System.out.println("IO problem with " + scriptFileName + e2);
		}
		catch(ScriptException e3) {
			System.out.println("ScriptException in " + scriptFileName + e3);
		}
		catch(NullPointerException e4) {
			System.out.println("Null ptr exception in " + scriptFileName + e4);
		}
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
		
		long modTime = paramFile.lastModified();
		if (modTime > fileLastModifiedTime)
		{ 
			long fileLastModifiedTime = modTime;
			this.executeScript("scripts/" + paramFile.getName());
			setupParams();
		}
		
		
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
