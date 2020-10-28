package a1.nodeControllers;

import ray.rage.scene.Node;
import ray.rage.scene.controllers.AbstractController;
import ray.rml.Degreef;

public class NodeMorphController extends AbstractController {

	float spinSpeed = 50f;
	float progress = 0f;
	float scale = 2f;
	
	public NodeMorphController() {}
	
	public NodeMorphController(float speed) {
		spinSpeed = speed;
	}
	
	@Override
	protected void updateImpl(float deltaTime) {
		
		deltaTime/=1000;
		
		progress+=deltaTime;
		
		if(progress > 1.5 * Math.PI ) progress -= Math.PI * 2;
		
		
		
		float scaleX = (float)Math.sin(2 * progress)/2 + 1;
		float scaleZ = (float)Math.sin(2 * progress + Math.PI)/2 + 1;
		
		//Scalar values follow a function of the sin wave over time
		for(Node n:super.controlledNodesList) {
			n.yaw(Degreef.createFrom(spinSpeed * deltaTime));
			n.setLocalScale(scaleX,1,scaleZ);
		}
	}

}
