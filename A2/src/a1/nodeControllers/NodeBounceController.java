package a1.nodeControllers;

import ray.rage.scene.Node;
import ray.rage.scene.controllers.AbstractController;
import ray.rml.Vector3f;

public class NodeBounceController extends AbstractController {

	private float bounceRate = 4f;
	private float bounceHeight = 0.5f;
	private float progress = 0;
	
	public NodeBounceController() {
		
	}
	
	public NodeBounceController(float rate, float height) {
		bounceRate = rate;
		bounceHeight = height;
	}
	
	@Override
	protected void updateImpl(float deltaTime) {
		
		progress+=deltaTime/1000;
		
		if(progress > Math.PI ) progress -= Math.PI * 2;
		
		//Vertical position follows a function of the sin wave over time
		for(Node n:super.controlledNodesList) {
			Vector3f position = (Vector3f)n.getLocalPosition();
			position = (Vector3f) Vector3f.createFrom(position.x(), (float)(bounceHeight*Math.sin(bounceRate * progress)), position.z());
			n.setLocalPosition(position);
		}
		
	}

}
