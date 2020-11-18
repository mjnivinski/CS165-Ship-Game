package a3.myGameEngine.flightControls;

import a3.myGameEngine.SimpleMath;
import ray.rage.Engine;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3;
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
	private float shipAccel = 5f;
	private float throttleAccel = 1f;
	private float throttleRate;
	
	
	private float vectorUpdateAccel = 1f;
	private float dampenRatio = 0.9f;
	private float moveX, moveY, moveZ;
	private Vector3 moveVector = Vector3f.createZeroVector();
	private Vector3 thrustVector;
	private float currentSpeed;
	
	
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
	
	float deltaTime;
	public void update() {
		
		deltaTime = eng.getElapsedTimeMillis()/1000;
		
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
		
		updatePosition();
	}
	
	private void updatePosition() {
		
		//thrust vector is made based on throttle value and ships forward axis
		Vector3 forward = ship.getWorldForwardAxis();
		thrustVector = Vector3f.createFrom(forward.x(),forward.y(),forward.z()).mult(throttle * shipSpeed);
		
		//move vector has thrust vector added to it.
		moveVector = moveVector.add(thrustVector.mult(vectorUpdateAccel * deltaTime));
		
		//move vector is dampened slightly
		moveVector = moveVector.mult(dampenRatio);
		
		//ships position is updated
		Vector3 position = ship.getLocalPosition();
		position = position.add(moveVector);
		ship.setLocalPosition(position);
	}
	
	private void pitch() {
		
		float value = keyVsGamepad(pitchForward, pitchBackward, controllerPitch);
		
		value = SimpleMath.parabolicSmooth(value);
		
		pitch = SimpleMath.lerp(pitch, value, pitchAccel * deltaTime);
		
		ship.pitch(Degreef.createFrom(pitchRate * pitch * deltaTime));
	}
	
	private void roll() {
		
		float value = keyVsGamepad(rollLeft, rollRight, controllerRoll);
		
		value = SimpleMath.parabolicSmooth(value);
		
		roll = SimpleMath.lerp(roll, value, rollAccel * deltaTime);
		
		ship.roll(Degreef.createFrom(rollRate * roll * deltaTime));
	}
	
	private void yaw() {
		
		float value = keyVsGamepad(yawLeft, yawRight, controllerYaw);
		
		value = SimpleMath.parabolicSmooth(value);
		
		yaw = SimpleMath.lerp(yaw, value, yawAccel * deltaTime);
		
		ship.yaw(Degreef.createFrom(yawRate * yaw * deltaTime));
	}
	
	private void throttle() {
		
		float value = keyVsGamepad(throttleUp, throttleDown, controllerThrottle);
		
		throttle+= throttleAccel * value * deltaTime;
		
		if(throttle > 1) throttle = 1;
		if(throttle < 0) throttle = 0;
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
	
	public float getThrottle() { return throttle; }
	public float getRoll() { return roll; }	
	public float getPitch() { return pitch; }	
	public float getYaw() { return yaw; }	
	
	
	private void print(String s) {
		System.out.println(s);
	}
}
