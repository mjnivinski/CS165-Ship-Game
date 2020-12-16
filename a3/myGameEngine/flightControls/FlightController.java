package a3.myGameEngine.flightControls;

import java.io.IOException;
import java.util.ArrayList;

import ris.MyGame;
import a3.myGameEngine.DeadZones;
import a3.myGameEngine.SimpleMath;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.physics.PhysicsEngine;
import ray.rage.Engine;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class FlightController {
	
	//comment
	private MyGame game;
	private SceneManager sm;
	private Engine eng;
	private PhysicsEngine physics;
	//private DolphinStateMachine machine;
	private Camera camera;
	private SceneNode cameraN;
	//private SceneNode dolphinN;
	
	//temporary for control test
	private SceneNode target;
	
	ShipController shipController;
	
	//Gamepad Abstract Action Classes
	
	ControllerThrottle CT;
	ControllerRoll CR;
	ControllerYaw CY;
	ControllerPitch CP;
	
	FireWeapon FW;
	
	//LeftHorizontal LH;
	//LeftVertical LV;
	//RightHorizontal RH;
	//RightVertical RV;
	//LeftBumper LB;
	//RightBumper RB;
	//GamepadThrottle GT;
	
	//Keyboard Abastract Action Classes
	
	RollLeft rollLeft;
	RollRight rollRight;
	PitchForward pitchForward;
	PitchBackward pitchBackward;
	YawLeft yawLeft;
	YawRight yawRight;
	ThrottleUp throttleUp;
	ThrottleDown throttleDown;
	
	KeyboardFireWeapon KFW;
	
	Vector3 cameraOffset = Vector3f.createFrom(0, 0f, -1.7f);
	
	float deltaTime;
	
	Vector3 basePosition;
	
	
	public FlightController(MyGame g, Camera c, SceneNode cN, SceneNode t, InputManager im, SceneManager sm, PhysicsEngine physics) throws IOException {
		print("ch-ch-ch-changes");
		game = g;
		camera = c;
		cameraN = cN;
		eng = game.getEngine();
		this.sm = sm;
		target = t;//
		setupInput(im);
		this.physics = physics;
		
		shipController = new ShipController(game, eng, this, target, sm, physics);
		
		cameraN.setLocalPosition(cameraOffset);
		
		camera.setPo((Vector3f) cameraN.getWorldPosition());
		
		camera.setFd((Vector3f) cameraN.getWorldForwardAxis().normalize());
		camera.setUp((Vector3f) cameraN.getWorldUpAxis().normalize());
		Vector3f rV = (Vector3f) cameraN.getWorldRightAxis();
		rV = (Vector3f) Vector3f.createFrom(-1 * rV.x(), rV.y(), rV.z());
		camera.setRt((Vector3f) rV.normalize());
		
		basePosition = cameraN.getLocalPosition();
	}
	
	private void setupInput(InputManager im) {
		String gamepadName = im.getFirstGamepadName();
		
		ArrayList<Controller> controllers = im.getControllers();
		ArrayList<String> keyboards = new ArrayList<String>();
		
		for (int i = 0; i < controllers.size(); i++) {
			if (controllers.get(i).getType() == Controller.Type.KEYBOARD)
				keyboards.add(controllers.get(i).getName());
		}
		
		//setup gamepad controls
		
		if(gamepadName != null) setupGamepad(im, gamepadName);
		
		//setup keyboard controls
		
		setupKeyboard(im);
	}
	
	private void setupGamepad(InputManager im, String controllerName) {
		
		CT  = new ControllerThrottle();
		CR = new ControllerRoll();
		CY = new ControllerYaw();
		CP = new ControllerPitch();
		FW = new FireWeapon();
		
		//LH = new LeftHorizontal();
		//LV = new LeftVertical();
		//RH = new RightHorizontal();
		//RV = new RightVertical();
		//LB = new LeftBumper();
		//RB = new RightBumper();
		//GT = new GamepadThrottle();

		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.X, CR,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.Y, CT,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.RX, CY,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.RY, CP,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.Z, FW,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		/*
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Button._4, LB,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Button._5, RB,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);*/
		
		/*im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.Z, GT,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);*/
		
	
		/*
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.POV, dPA,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Button._0, aBA,
				InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		*/
	}
	
	private void setupKeyboard(InputManager im) {
		ArrayList<Controller> controllers = im.getControllers();
		ArrayList<String> keyboards = new ArrayList<String>();
		
		for (int i = 0; i < controllers.size(); i++) {
			if (controllers.get(i).getType() == Controller.Type.KEYBOARD)
				keyboards.add(controllers.get(i).getName());
		}
		
		rollLeft = new RollLeft();
		rollRight = new RollRight();
		pitchForward = new PitchForward();
		pitchBackward = new PitchBackward();
		throttleUp = new ThrottleUp();
		throttleDown = new ThrottleDown();
		yawLeft = new YawLeft();
		yawRight = new YawRight();
		KFW = new KeyboardFireWeapon();
		
		for (int i = 0; i < keyboards.size(); i++) {
			
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.Q, rollLeft,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.E, rollRight,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.S, pitchForward,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.W, pitchBackward,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.LSHIFT, throttleUp,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.LCONTROL, throttleDown,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.A, yawLeft,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.D, yawRight,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.SPACE, KFW,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		}
	}
	
	//private void cameraLook() {
	//	print("\nfd: " + cameraN.getWorldPosition());
	//}
	
	public void update() {
		
		
		
		deltaTime = eng.getElapsedTimeMillis()/1000;
		//print("start");
		
		//cameraLook();
		shipController.update();
		//cameraLook();
		updateCamera();
		//cameraLook();
		
		//print("finish");
	}
	
	/*****************************************************************
	 * GAMEPAD SECTION												 *
	 *****************************************************************
	 */
	
	public float parabolicSmooth(float v) {
		if(v < 0) {
			v*=v;
			v*=-1;
		}
		else v*=v;
		
		return v;
	}
	
	private class ControllerThrottle extends AbstractInputAction {
		float value;
		
		@Override
		public void performAction(float arg0, Event e) {
			
			if(Math.abs(e.getValue()) < 0.2) value = 0;
			else value = -1 * e.getValue();
			
			value = parabolicSmooth(value);
			
			shipController.setControllerThrottle(value);
		}
	}
	
	
	private class ControllerRoll extends AbstractInputAction {
		float value;
		
		@Override
		public void performAction(float arg0, Event e) {
			if(Math.abs(e.getValue()) > 0.25f) value = e.getValue();
			else value = 0;
			
			shipController.setControllerRoll(value);
		}
	}
	
	private class ControllerYaw extends AbstractInputAction {
		
		float value;
		
		@Override
		public void performAction(float arg0, Event e) {
			if(Math.abs(e.getValue()) > 0.2f) value = -1 * e.getValue();
			else value = 0;
			
			shipController.setControllerYaw(value);
		}
	}
	
	private class ControllerPitch extends AbstractInputAction {
		//float pValue;
		//float value;
		float eValue;
		//float smoothSpeed = 15f;
		//float offset = 2;
				
		@Override
		public void performAction(float arg0, Event e) {
			if(Math.abs(e.getValue()) > 0.25f) eValue = -1 * e.getValue();
			else eValue = 0;
			
			shipController.setControllerPitch(eValue);
		}
	}
	
	/*
	private class RightBumper extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//shipController.setRightBumper(e.getValue());
		}
	}
	
	private class LeftBumper extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//shipController.setLeftBumper(e.getValue());
		}
	}*/
	
	/*****************************************************************
	 * KEYBOARD SECTION												 *
	 *****************************************************************
	 */
	
	private class RollLeft extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//print("rollLeft");
			shipController.setRollLeft(-1 * e.getValue());
		//	ris.MyGame.throttleLeftAndBackAnimation();
		}
	}
	
	private class RollRight extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//print("rollRight");
			shipController.setRollRight(-1 * e.getValue());
		//	ris.MyGame.throttleRightAndBackAnimation();
		}
	}
	
	private class PitchForward extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			shipController.setPitchFoward(e.getValue());
		}
	}
	
	private class PitchBackward extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			shipController.setPitchBackward(e.getValue());
		}
	}
	
	private class YawRight extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			shipController.setYawRight(e.getValue());
		//	ris.MyGame.throttleRightAndBackAnimation();
		}
	}
	
	private class YawLeft extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			shipController.setYawLeft(e.getValue());
		//	ris.MyGame.throttleLeftAndBackAnimation();
		}
	}
	
	private class ThrottleUp extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			shipController.setThrottleUp(e.getValue());
		//	ris.MyGame.throttleUpAndBackAnimation();
		}
	}
	
	private class ThrottleDown extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			shipController.setThrottleDown(e.getValue());
		//	ris.MyGame.throttleDownAndBackAnimation();
		}
	}
	
	private class FireWeapon extends AbstractInputAction {
		
		
		@Override
		public void performAction(float arg0, Event e) {
			if( e.getValue() < -0.1f) shipController.setFiring(true);
			else shipController.setFiring(false);
		}
	}
	
	private class KeyboardFireWeapon extends AbstractInputAction {
		
		
		@Override
		public void performAction(float arg0, Event e) {
			//if(e.getValue())
			//print("Keyboard: " + e.getValue());
			if(e.getValue() == 1) shipController.setFiring(true);
			else shipController.setFiring(false);
		}
	}
	
	
	float timer = 0;
	
	public void updateCamera() {
		
		camera.setFd((Vector3f) target.getWorldForwardAxis().normalize());
		camera.setUp((Vector3f) target.getWorldUpAxis().normalize());
		Vector3f rV = (Vector3f) target.getWorldRightAxis();
		rV = (Vector3f) Vector3f.createFrom(-1 * rV.x(), -1 * rV.y(), -1 * rV.z());
		camera.setRt((Vector3f) rV.normalize());
		
		cameraN.setLocalPosition(cameraN.getLocalPosition());
		camera.setPo((Vector3f) cameraN.getWorldPosition());
		
		//do not uncomment camera.setPo((Vector3f) target.getWorldPosition());
		
		cameraThrottleShift();
		cameraTurnShift();
	}
	
	
	private void cameraThrottleShift() {
		
		float throttleDampen = 0.025f;
		
		Vector3 forward = camera.getFd();
		
		forward = forward.mult(1 + (shipController.getThrottle() * throttleDampen));
		
		camera.setFd((Vector3f) forward);
	}
	
	private void cameraTurnShift() {
		
		float rollRatio = 0.03f;
		float pitchRatio = 0.04f;
		float yawRatio = 0.025f;
		
		//the ship rolls the camera shifts slightly in the opposite direction
		//negative left. positive right
		Vector3 rollVector = cameraN.getLocalRightAxis().mult(shipController.getRoll()).mult(rollRatio);
		
		//when the ship rolls, the perspective leans into that direction and slightly downward.
		// -(x^2)/20
		float parabolaY = shipController.getRoll();
		parabolaY *= -1 * parabolaY/20;
		rollVector = rollVector.add(0,parabolaY,0);
		
		//the ship pitches the camera shifts slightly in the opposite direction
		//pitch up negative. pitch down positive
		Vector3 pitchVector = cameraN.getLocalUpAxis().mult(shipController.getPitch()).mult(pitchRatio);
		
		//the ship yaws the camera shifts slightly in the opposite direction
		//positive left, negative right
		Vector3 yawVector = cameraN.getLocalRightAxis().mult(shipController.getYaw()).mult(yawRatio);
		
		//Add all the vectors together
		cameraN.setLocalPosition(basePosition.add(rollVector).add(pitchVector).add(yawVector));
	}
	
	public int getThrottleSign() {
		float throttle = shipController.getThrottle();
		int sign = 0;
		if(throttle > 0) sign = 1;
		else if(throttle < 0) sign = -1;
		
		return sign;
	}
	public int getPitchSign() {
		float pitch = shipController.getThrottle();
		int sign = 0;
		if(pitch > 0) sign = 1;
		else if(pitch < 0) sign = -1;
		
		return sign;
	}
	public int getRollSign() {
		float roll = shipController.getThrottle();
		int sign = 0;
		if(roll > 0) sign = 1;
		else if(roll < 0) sign = -1;
		
		return sign;
	}
	public int getYawSign() {
		float yaw = shipController.getThrottle();
		int sign = 0;
		if(yaw > 0) sign = 1;
		else if(yaw < 0) sign = -1;
		
		return sign;
	}
	
	private void print(String s) {
		System.out.println(s);
	}
}

















