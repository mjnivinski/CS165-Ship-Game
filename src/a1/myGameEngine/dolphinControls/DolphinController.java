package a1.myGameEngine.dolphinControls;

import java.util.ArrayList;

import a3.MyGame;
import a3.myGameEngine.DeadZones;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.rage.Engine;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3f;

public class DolphinController {
	
	private MyGame game;
	private Engine eng;
	private DolphinStateMachine machine;
	private Camera camera;
	private SceneNode cameraN;
	private SceneNode dolphinN;
	private float cameraAzimuth = 0f;
	private float cameraElevation = 15.0f;
	private float radius = 2.0f;
	//private Vector3f targetPos;
	private Vector3f worldUp;
	private RightVerticalAction rVA;
	private RightHorizontalAction rHA;
	private LeftVerticalAction lVA;
	private LeftHorizontalAction lHA;
	private AButtonAction aBA;
	private DPadAction dPA;
	
	private ArrowRightAction aRA;
	private ArrowLeftAction aLA;
	private ArrowUpAction aUA;
	private ArrowDownAction aDA;
	private AKeyAction aKA;
	private DKeyAction dKA;
	private WKeyAction wKA;
	private SKeyAction sKA;
	private RKeyAction rKA;
	private FKeyAction fKA;
	
	private float orbitSpeed = 100f;
	private float rotateSpeed = 50f;
	private float zoomSpeed = 2.5f;
	private float pitchSpeed = 80f;
	private float moveSpeed = 5f;
	
	
	private Vector3f heading;
	
	private float speed;
	private float verticalSpeed;
	
	private boolean isGamePad;
	
	public DolphinController(MyGame game, Camera c, SceneNode cN, SceneNode t, InputManager im, String controllerName) {
		
		this.game = game;
		eng = game.getEngine();
		camera = c;
		cameraN = cN;
		dolphinN = t;
		worldUp = (Vector3f) Vector3f.createFrom(0,1f,0);
		
		setupInput(im, controllerName);
		
		machine = new DolphinStateMachine(eng, this);
		
	}
	
	//Contains logic for setting up both gamepad and keyboard inputs
	private void setupInput(InputManager im, String controllerName) {
		
		if(controllerName == null) return;
		
		//I use this same action for the spacebar
		aBA = new AButtonAction();
		
		if(im.getFirstGamepadName() == controllerName) {
		
			isGamePad = true;
			
			rVA = new RightVerticalAction();
			rHA = new RightHorizontalAction();
			lVA = new LeftVerticalAction();
			lHA = new LeftHorizontalAction();
			
			dPA = new DPadAction();
			
			im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.RY, rVA,
					InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
			im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.RX, rHA,
					InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
			im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.Y, lVA,
					InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
			im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.X, lHA,
					InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			im.associateAction(controllerName, net.java.games.input.Component.Identifier.Axis.POV, dPA,
					InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			
			im.associateAction(controllerName, net.java.games.input.Component.Identifier.Button._0, aBA,
					InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			
			
		}
		else {
			
			isGamePad = false;
			
			ArrayList<Controller> controllers = im.getControllers();
			ArrayList<String> keyboards = new ArrayList<String>();
			
			for (int i = 0; i < controllers.size(); i++) {
				if (controllers.get(i).getType() == Controller.Type.KEYBOARD)
					keyboards.add(controllers.get(i).getName());
			}
			
			aRA = new ArrowRightAction();
			aLA = new ArrowLeftAction();
			aUA = new ArrowUpAction();
			aDA = new ArrowDownAction();
			aKA = new AKeyAction();
			dKA = new DKeyAction();
			wKA = new WKeyAction();
			sKA = new SKeyAction();
			rKA = new RKeyAction();
			fKA = new FKeyAction();
			
			for (int i = 0; i < keyboards.size(); i++) {
				
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.RIGHT, aRA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.LEFT, aLA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.UP, aUA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.DOWN, aDA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.A, aKA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.D, dKA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.W, wKA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.S, sKA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.R, rKA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.F, fKA,
						InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
				
				im.associateAction(keyboards.get(i), net.java.games.input.Component.Identifier.Key.SPACE, aBA,
						InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			}
		}
		
	}
	
	
	/*
	 * The format of this section groups the actual input classes together, then the relative setters and getters for those actions
	 * And then the next grouped action classes
	 */
	
	
	//Dpad and R/F keys pitch the camera
	private class DPadAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			
			if(e.getValue() == 0) return;
			if(e.getValue() > 0.5f && e.getValue() < 1) pitch(-1);
			else if(e.getValue() < 0.5f || e.getValue() > 0) pitch(1);
		}
	}
	
	private class RKeyAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			pitch(1);
		}
	}
	
	private class FKeyAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			pitch(-1);
		}
	}
	
	private void pitch(float e) {
		machine.pitch(e);
	}
	
	public float getPitchSpeed() { return pitchSpeed; }
	public float getElevation() { return cameraElevation; }
	public void setElevation(float e) {
		if(e < 10 || e > 50) return;
		else cameraElevation = e;
	}
	
	
	//Right Vertical Stick and Up/Down arrow keys zoom the camera in/out
	private class RightVerticalAction extends AbstractInputAction {

		@Override
		public void performAction(float arg0, Event e) {
			DeadZones.RVertical = e.getValue();
			machine.rightVertical(e.getValue());
		}
	}
	
	private class ArrowUpAction extends AbstractInputAction {

		@Override
		public void performAction(float arg0, Event e) {
			machine.rightVertical(-1 * e.getValue());
			
		}
	}
	
	private class ArrowDownAction extends AbstractInputAction {

		@Override
		public void performAction(float arg0, Event e) {
			
			machine.rightVertical(e.getValue());
			
		}
	}
	
	public float getRadius() { return radius; }
	public void setRadius(float r) { radius = r; }
	public float getZoomSpeed() { return zoomSpeed; }
	
	
	//Right Stick Horizontal and Arrow Right/Left Keys handle orbiting camera about the dolphin
	private class RightHorizontalAction extends AbstractInputAction {
		public void performAction(float arg0, Event e) {
			DeadZones.RHorizontal = e.getValue();
			machine.rightHorizontal(e.getValue());
		}
	}
	
	private class ArrowRightAction extends AbstractInputAction {

		@Override
		public void performAction(float arg0, Event e) {
			machine.rightHorizontal(e.getValue());
			
		}
	}
	
	private class ArrowLeftAction extends AbstractInputAction {

		@Override
		public void performAction(float arg0, Event e) {
			
			machine.rightHorizontal(-1 * e.getValue());
			
		}
	}
	
	public float getAzimuth() { return cameraAzimuth; }
	public void setAzimuth(float azimuth) { cameraAzimuth = azimuth; }
	public float getOrbitSpeed() { return orbitSpeed; }
	
	//Left Stick Horizontal and A/D keys handle rotating dolphin
	private class LeftHorizontalAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			DeadZones.LHorizontal = e.getValue();
			machine.leftHorizontal(e.getValue());
		}
	}
	
	private class AKeyAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			machine.leftHorizontal(-1 * e.getValue());
		}
	}
	
	private class DKeyAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			machine.leftHorizontal(e.getValue());
		}
	}
	
	public float getRotateSpeed() { return rotateSpeed; }
	public SceneNode getDolphinNode() { return dolphinN; }
	
	//Left Vertical and W/S keys handle moving dolphin forward/backward
	private class LeftVerticalAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			DeadZones.LVertical = e.getValue();
			machine.leftVertical(e.getValue());
		}
	}
	
	private class WKeyAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			speed = -1;
			machine.leftVertical(-1 * e.getValue());
		}
	}
	
	private class SKeyAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			speed = 1;
			machine.leftVertical(e.getValue());
		}
	}
	
	
	//A button action is the South Gamepad button, as well spacebar is connected to this, it triggers the jump for the dolphin
	private class AButtonAction extends AbstractInputAction {
		@Override
		public void performAction(float arg0, Event e) {
			
			if(machine.isMoving()) heading = (Vector3f) dolphinN.getLocalForwardAxis();
			
			machine.jump(e.getValue());
		}
	}
	
	public Vector3f getHeading() { return heading; }
	public void setHeading(Vector3f h) { heading = h; }
	public void setSpeed(float s) { speed = s; }
	public float getSpeed() { return speed; }
	public float getMoveSpeed() { return moveSpeed; }
	public float getVerticalSpeed() { return verticalSpeed; }
	public void setVerticalSpeed(float s) { verticalSpeed = s; }
	public boolean getIsGamePad() { return isGamePad; }
	
	
	//Update methods called for changing the position of the camera/dolphin
	private void updateCameraPosition() {
		
		double theta = Math.toRadians(cameraAzimuth);
		double phi = Math.toRadians(cameraElevation);
		double x = radius * Math.cos(phi) * Math.sin(theta);
		double y = radius * Math.sin(phi);
		double z = radius * Math.cos(phi) * Math.cos(theta);
		cameraN.setLocalPosition(Vector3f.createFrom((float)x,(float)y,(float)z).add(dolphinN.getWorldPosition()));
		cameraN.lookAt(dolphinN, worldUp);
	}
	
	private void updateStateMachine() {
		
		machine.update();
	}
	
	public void update() {
		updateStateMachine();
		updateCameraPosition();
	}
}
