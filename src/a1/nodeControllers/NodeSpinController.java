package a1.nodeControllers;

import ray.rage.scene.Node;
import ray.rage.scene.controllers.AbstractController;
import ray.rml.Degreef;

public class NodeSpinController extends AbstractController {

	float spinSpeed = 50f;
	
	public NodeSpinController() {}
	
	public NodeSpinController(float speed) {
		spinSpeed = speed;
	}
	
	@Override
	protected void updateImpl(float deltaTime) {
		
		deltaTime = deltaTime/1000;
		
		for(Node n:super.controlledNodesList) {
			n.yaw(Degreef.createFrom(spinSpeed * deltaTime));
		}
	}

}
