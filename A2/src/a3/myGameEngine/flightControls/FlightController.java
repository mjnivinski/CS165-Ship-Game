package a3.myGameEngine.flightControls;

import java.util.ArrayList;

import a3.MyGame;
import a3.myGameEngine.DeadZones;
import a3.myGameEngine.SimpleMath;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.rage.Engine;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3f;

public class FlightController {
	
	private MyGame game;
	private Engine eng;
	//private DolphinStateMachine machine;
	private Camera camera;
	private SceneNode cameraN;
	//private SceneNode dolphinN;
	
	//temporary for control test
	private SceneNode target;
	
	ShipController shipController;
	
	//Gamepad Abstract Action Classes
	
	LeftHorizontal LH;
	LeftVertical LV;
	RightHorizontal RH;
	RightVertical RV;
	
	//Keyboard Abastract Action Classes
	
	RollLeft rollLeft;
	RollRight rollRight;
	PitchForward pitchForward;
	PitchBackward pitchBackward;
	
	//Vector3f offset = (Vector3f) Vector3f.createFrom(0, 0.4f, -0.5f);
	Vector3f offset = (Vector3f) Vector3f.createFrom(0, 0.4f, -0.5f);
	
	float deltaTime;
	
	
	public FlightController(MyGame g, Camera c, SceneNode cN, SceneNode t, InputManager im) {
		game = g;
		camera = c;
		cameraN = cN;
		eng = game.getEngine();
		
		target = t;
		setupInput(im);
		
		shipController = new ShipController(eng, this, target);
		
		cameraN.setLocalPosition(offset);
		//cameraN.setLocalPosition(0,0,0);
		
		
		camera.setMode('c');
		
		//Vector3f v = (Vector3f) camera.getParentNode().getWorldPosition();
		
		camera.setPo((Vector3f) cameraN.getWorldPosition());
		
		
		camera.setFd((Vector3f) cameraN.getWorldForwardAxis().normalize());
		camera.setUp((Vector3f) cameraN.getWorldUpAxis().normalize());
		Vector3f rV = (Vector3f) cameraN.getWorldRightAxis();
		rV = (Vector3f) Vector3f.createFrom(-1 * rV.x(), rV.y(), rV.z());
		camera.setRt((Vector3f) rV.normalize());
		
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
		
		LH = new LeftHorizontal();
		LV = new LeftVertical();
		RH = new RightHorizontal();
		RV = new RightVertical();

		//im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.X, LH,
		//		InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.Y, LV,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.RX, RH,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.RY, RV,
				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
	
		
	
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
		
		for (int i = 0; i < keyboards.size(); i++) {
			
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.A, rollLeft,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.D, rollRight,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.W, pitchForward,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
			
			im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.S, pitchBackward,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
		}
	}
	
	private void cameraLook() {
		print("\nfd: " + cameraN.getWorldPosition());
	}
	
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
	
	
	private class LeftHorizontal extends AbstractInputAction {
		float value;
		
		@Override
		public void performAction(float arg0, Event e) {
			if(Math.abs(e.getValue()) > 0.25f) value = -1 * e.getValue();
			else value = 0;
			
			shipController.setLeftHorizontal(value);
		}
	}
	
	private class LeftVertical extends AbstractInputAction {
		
		float pValue;
		float value;
		float eValue;
		float smoothSpeed = 15f;
		//float offset = 2;
		
		@Override
		public void performAction(float arg0, Event e) {
			if(Math.abs(e.getValue()) > 0.25f) eValue = -1 * e.getValue();
			
			
			/*
			else eValue = 0;
			
			value = SimpleMath.lerp(pValue, eValue, smoothSpeed * deltaTime);
			
			pValue = value;
			
			if(value < 0) {
				value *= value;
				value *= -1;
			}
			else value *= value;
			
			shipController.setLeftVertical(pValue);
			*/
			shipController.setLeftVertical(eValue);
		}
	}
	
	private class RightHorizontal extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//if(Math.abs(e.getValue()) > 0.5f) System.out.println("Right Horizontal: " + e.getValue());
			shipController.setRightHorizontal(e.getValue());
		}
	}
	
	private class RightVertical extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//if(Math.abs(e.getValue()) > 0.5f) System.out.println("Right Vertical: " + e.getValue());
			shipController.setRightVertical(e.getValue());
			
			
			//range 0.5 to 20 for example
			
			float value = -1 * e.getValue();
			
			if(value < 0) {
				value = 1 + value * 0.5f;
			}else {
				value = 1 + value * 20; 
			}
			
			camera.setFd((Vector3f) camera.getFd().normalize());
			camera.setFd((Vector3f) camera.getFd().mult(value));
			
			
			//We have to have it approach the value over time
			//Maybe the stick determines an accelerated value
		}
	}
	
	/*****************************************************************
	 * KEYBOARD SECTION												 *
	 *****************************************************************
	 */
	
	private class RollLeft extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//print("rollLeft");
			shipController.setRollLeft(e.getValue());
		}
	}
	
	private class RollRight extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//print("rollRight");
			shipController.setRollRight(e.getValue());
		}
	}
	
	private class PitchForward extends AbstractInputAction {
		
		float pValue;
		float value;
		float eValue;
		float smoothSpeed = 15f;
		
		@Override
		public void performAction(float arg0, Event e) {
			
			shipController.setPitchFoward(e.getValue());
			
			
		}
	}
	
	private class PitchBackward extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			//print("pitchBackward");
			shipController.setPitchBackward(e.getValue());
		}
	}
	
	
	float timer = 0;
	
	public void updateCamera() {
		
		camera.setFd((Vector3f) target.getWorldForwardAxis().normalize());
		camera.setUp((Vector3f) target.getWorldUpAxis().normalize());
		Vector3f rV = (Vector3f) target.getWorldRightAxis();
		rV = (Vector3f) Vector3f.createFrom(-1 * rV.x(), rV.y(), rV.z());
		camera.setRt((Vector3f) rV.normalize());
		
		cameraN.setLocalPosition(cameraN.getLocalPosition());
		camera.setPo((Vector3f) cameraN.getWorldPosition());
		
		
		//camera.setPo((Vector3f) target.getWorldPosition());
	}
	
	private void print(String s) {
		System.out.println(s);
	}
}

















