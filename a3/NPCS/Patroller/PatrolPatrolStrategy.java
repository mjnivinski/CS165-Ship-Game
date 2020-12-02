package a3.NPCS.Patroller;

import ray.rage.scene.SceneNode;

public class PatrolPatrolStrategy implements PatrolStrategy {

	SceneNode npc;
	SceneNode target; //might replace target with position
	float radius;
	
	public PatrolPatrolStrategy(SceneNode n, SceneNode t, float r) {
		npc = n;
		target = t;
		radius = r;
	}
	
	@Override
	public void move(float deltaTime) {
		//move npc around target at Z = 0
	}
}
