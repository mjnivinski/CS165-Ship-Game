package a3.NPCS.Patroller;

import a3.myGameEngine.VectorMath;
import ray.rage.scene.SceneNode;

public class PatrolReturnStrategy implements PatrolStrategy {

	SceneNode npc;
	SceneNode target; //might replace target with position
	float radius;
	
	public PatrolReturnStrategy(SceneNode n, SceneNode t, float r) {
		npc = n;
		target = t;
		radius = r;
	}
	
	@Override
	public void move(float deltaTime) {
		//move npc back to the defended node
		
		
	}
	
	
}
