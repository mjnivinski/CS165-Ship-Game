package a1.myGameEngine.dolphinControls;

import a3.myGameEngine.DeadZones;
import ray.rage.Engine;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

//State machine design pattern implementation as internal private classes

public class DolphinStateMachine {
	
	private Idle idle;
	private Moving moving;
	private Jumping jumping;
	
	private IDolphinState state;
	
	private Engine eng;
	DolphinController dolphinController;
	
	public DolphinStateMachine(Engine eng, DolphinController dC) {
		
		idle = new Idle();
		moving = new Moving();
		jumping = new Jumping();
		
		state = idle;
		
		this.eng = eng;
		dolphinController = dC;
	}
	
	public void changeState(IDolphinState state) {
		this.state = state;
	}
	
	public boolean isMoving() {
		if(state == moving) return true;
		return false;
	}
	
	private IDolphinState getIdle() { return idle; }
	private IDolphinState getMoving() { return moving; }
	private IDolphinState getJumping() { return jumping; }
	
	public void update() {
		state.update();
	}
	
	public void rightHorizontal(float e) {
		state.rightHorizontal(e);
	}
	
	public void rightVertical(float e) {
		state.rightVertical(e);
	}
	
	public void leftHorizontal(float e) {
		state.leftHorizontal(e);
	}
	
	public void leftVertical(float e) {
		state.leftVertical(e);
	}
	
	public void pitch(float e) {
		state.pitch(e);
	}
	
	public void jump(float e) {
		state.jump(e);
	}
	
	private interface IDolphinState{
		void update();
		void rightHorizontal(float e);
		void rightVertical(float e);
		void leftHorizontal(float e);
		void leftVertical(float e);
		void pitch(float e);
		void jump(float e);
	}
	
	//Idle state
	private class Idle implements IDolphinState{

		@Override
		public void update() {
			
		}
		@Override
		public void rightHorizontal(float e) {
			rotateCamera(e);
		}

		@Override
		public void rightVertical(float e) {
			zoomInOut(e);
		}

		@Override
		public void leftHorizontal(float e) {
			rotateDolphin(e);
		}

		@Override
		public void leftVertical(float e) {
			if(Math.abs(e) > 0.3f) {
				changeState(getMoving());
			}
		}
		
		@Override
		public void pitch(float e) { 
			pitchCamera(e);
		}
		
		@Override
		public void jump(float e) {
			//Can't jump while not moving
		}
	}
	
	//Moving state
	private class Moving implements IDolphinState{

		@Override
		public void update() {
			//special non controller based logic would go here.
			//for instance if there was momentum, not reliant on current input.
			//This method would handle updating the dolphin
		}
		@Override
		public void rightHorizontal(float e) {
			rotateCamera(e);
		}

		@Override
		public void rightVertical(float e) {
			zoomInOut(e);
		}

		@Override
		public void leftHorizontal(float e) {
			// TODO Auto-generated method stub
			
			rotateDolphin(e);
		}

		@Override
		public void leftVertical(float e) {
			// TODO Auto-generated method stub
			if(Math.abs(e) < 0.3f) {
				changeState(getIdle());
			}
			
			moveDolphinForward(e);
		}
		
		@Override
		public void pitch(float e) { 
			pitchCamera(e);
		}
		
		@Override
		public void jump(float e) {
			dolphinController.setHeading((Vector3f)dolphinController.getHeading());
			if(dolphinController.getIsGamePad()) dolphinController.setSpeed(DeadZones.LVertical);
			dolphinController.setVerticalSpeed(4.0f);
			jumpSpeed = dolphinController.getSpeed();
			changeState(getJumping());
		}
	}
	
	private float jumpSpeed;
	
	private class Jumping implements IDolphinState{

		@Override
		public void update() {
			float deltaTime = eng.getElapsedTimeMillis()/1000;
			SceneNode dolphinN = dolphinController.getDolphinNode();
			float verticalSpeed = dolphinController.getVerticalSpeed();
			
			moveForwardHeading(jumpSpeed);
			
			if(dolphinN.getWorldPosition().y() + (verticalSpeed * deltaTime) < 0) {
				Vector3f position = (Vector3f)dolphinN.getWorldPosition();
				dolphinN.setLocalPosition(position.x(), 0, position.z());
				changeState(getIdle());
			} else {
				dolphinN.moveUp(verticalSpeed * deltaTime);
				verticalSpeed -= deltaTime * 5;
				dolphinController.setVerticalSpeed(verticalSpeed);
			}
			
		}
		@Override
		public void rightHorizontal(float e) {
			rotateCamera(e);
		}

		@Override
		public void rightVertical(float e) {
			zoomInOut(e);
		}

		@Override
		public void leftHorizontal(float e) {
			rotateDolphin(e);
		}

		@Override
		public void leftVertical(float e) {
			//Nothing
		}
		
		@Override
		public void pitch(float e) { 
			pitchCamera(e);
		}
		
		@Override
		public void jump(float e) {
			
			//jump does nothing while already jumping
		}
	}
	
	//These are the various actions that each state can access
	
	private void rotateCamera(float e) {
		//DeadZones.RHorizontal = e.getValue();
		if(DeadZones.RMagnitude() < 0.4 && Math.abs(e) < 1) return;
		
		float cameraAzimuth = dolphinController.getAzimuth();
		float orbitSpeed = dolphinController.getOrbitSpeed();
		float deltaTime = eng.getElapsedTimeMillis()/1000;
		
		cameraAzimuth += deltaTime * (-1 * orbitSpeed * e);
		cameraAzimuth += 360;
		cameraAzimuth %= 360;

		dolphinController.setAzimuth(cameraAzimuth);
	}
	
	private void zoomInOut(float e) {
		//DeadZones.RVertical = e.getValue();
		if(DeadZones.RMagnitude() < 0.4 && Math.abs(e) < 1) return;
		
		float deltaTime = eng.getElapsedTimeMillis()/1000;
		float radius = dolphinController.getRadius();
		float zoomSpeed = dolphinController.getZoomSpeed();
		
		radius += deltaTime * (zoomSpeed * e);
		radius %= radius + 360;
		
		if(radius < 1.0f) radius = 1.0f;
		else if(radius > 3.5f) radius = 3.5f;
		
		dolphinController.setRadius(radius);
	}
	
	private void rotateDolphin(float e) {
		if(DeadZones.LMagnitude() < 0.4f && Math.abs(e) < 1) return;
		
		float deltaTime = eng.getElapsedTimeMillis()/1000;
		float rotateSpeed = dolphinController.getRotateSpeed();
		
		float rotateAmount = -1 * rotateSpeed * e * deltaTime;
		float newAzimuth = (rotateAmount + dolphinController.getAzimuth() + 360) % 360;
		dolphinController.setAzimuth(newAzimuth);
		dolphinController.getDolphinNode().yaw(Degreef.createFrom(rotateAmount));
	}

	private void moveDolphinForward(float e) {
		
		float deltaTime = eng.getElapsedTimeMillis()/1000;
		dolphinController.getDolphinNode().moveForward(-1 * dolphinController.getMoveSpeed() * e * deltaTime);
	}
	
	private void moveForwardHeading(float e) {
		
		float deltaTime = eng.getElapsedTimeMillis()/1000;
		float moveSpeed = dolphinController.getMoveSpeed();
		
		Vector3f heading = (Vector3f)dolphinController.getHeading().mult(-1 * moveSpeed * e * deltaTime);

		SceneNode n = dolphinController.getDolphinNode();
		Vector3f position = (Vector3f)n.getLocalPosition();
		
		position = (Vector3f)position.add(heading);
		n.setLocalPosition(position);
	}
	
	private void pitchCamera(float e) {
		float deltaTime = eng.getElapsedTimeMillis()/1000;
		
		dolphinController.setElevation(dolphinController.getElevation() + (e * dolphinController.getPitchSpeed() * deltaTime));
	}
	
}
