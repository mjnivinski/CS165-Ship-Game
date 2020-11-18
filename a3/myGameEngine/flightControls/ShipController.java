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
	private float yawAccel = 5f;
	private float shipSpeed = 10f;
	private float throttleAccel = 1f;
	private float throttleRate;
	
	
	//private float pitch, leftVertical, pitchForward, pitchBackward;
	private float pitch, pitchForward, pitchBackward, controllerPitch;
	
	private float roll, rollLeft, rollRight, controllerRoll;
	
	private float yaw, yawLeft, yawRight, controllerYaw;
	
	private float throttle, throttleUp, throttleDown, controllerThrottle;
	
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
		//float value;
		//float keyValue = pitchForward - pitchBackward;
		
		float value = keyVsGamepad(pitchForward, pitchBackward, controllerPitch);
		
		/*
		if(keyValue > 0) {
			if(controllerPitch > 0) value = 1;
			else value = keyValue + controllerPitch;
		}
		else if(keyValue < 0) {
			if(controllerPitch <= 0) value = -1;
			else value = keyValue + controllerPitch;
		}
		else {
			value = controllerPitch;
		}*/
		
		if(value < 0) {
			value *= value;
			value *= -1;
		}
		else value *= value;
		
		
		pitch = SimpleMath.lerp(pitch, value, pitchAccel * deltaTime);
		
		ship.pitch(Degreef.createFrom(pitchRate * pitch * deltaTime));
		
	}
	
	private void roll() {
		
		float value = keyVsGamepad(rollLeft, rollRight, controllerRoll);
		
		if(value < 0) {
			value *= value;
			value *= -1;
		}
		else value *= value;
		
		
		roll = SimpleMath.lerp(roll, value, rollAccel * deltaTime);
		
		ship.roll(Degreef.createFrom(rollRate * roll * deltaTime));
	}
	
	private void yaw() {
		
		float value = keyVsGamepad(yawLeft, yawRight, controllerYaw);
		
		if(value < 0) {
			value *= value;
			value *= -1;
		}
		else value *= value;
		
		yaw = SimpleMath.lerp(yaw, value, yawAccel * deltaTime);
		
		ship.yaw(Degreef.createFrom(yawRate * value * deltaTime));
	}
	
	private void throttle() {
		
		float value = keyVsGamepad(throttleUp, throttleDown, controllerThrottle);
		
		throttle+= throttleAccel * value * deltaTime;
		
		if(throttle > 1) throttle = 1;
		if(throttle < 0) throttle = 0;
		
		System.out.println(throttle);
		
		ship.moveForward(shipSpeed * throttle * deltaTime);
	}
	
	private float keyVsGamepad(float keyPos, float keyNeg, float controllerValue) {
		float value;
		
		float keyValue = keyPos - keyNeg;
		
		if(keyValue > 0) {
			if(controllerValue > 0) value = keyValue;
			else value = keyValue + controllerValue;
		}
		else if(keyValue < 0) {
			if(controllerValue < 0) value = keyValue;
			else value = keyValue + controllerValue;
		}
		else value = controllerValue;
		
		return value;
	}
	
	public void setControllerThrottle(float v) { controllerThrottle = v;}
	public void setControllerRoll(float v) { controllerRoll = v;}
	public void setControllerPitch(float v) { controllerPitch = v;}
	public void setControllerYaw(float v) { controllerYaw = v;}
	
	public void setRollLeft(float v) { rollLeft = v;}
	public void setRollRight(float v) { rollRight = v;}
	public void setPitchFoward(float v) { pitchForward = v;}
	public void setPitchBackward(float v) { pitchBackward = v;}
	public void setYawLeft(float v) { yawLeft = v; }
	public void setYawRight(float v) { yawRight = v; }
	public void setThrottleUp(float v) { throttleUp = v; }
	public void setThrottleDown(float v) { throttleDown = v; }
}
