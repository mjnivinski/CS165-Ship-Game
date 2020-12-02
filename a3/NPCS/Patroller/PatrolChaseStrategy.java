package a3.NPCS.Patroller;

import ray.rage.scene.SceneNode;

public class PatrolChaseStrategy implements PatrolStrategy {

	private SceneNode npc;
	private SceneNode target;
	
	public PatrolChaseStrategy(SceneNode n, SceneNode t) {
		npc = n;
		target = t;
	}
	
	@Override
	public void move(float deltaTime) {
		
	}
}
