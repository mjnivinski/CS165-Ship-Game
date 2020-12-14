package a3.NPCS.Patroller;

import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class PatrolPatrolStrategy implements PatrolStrategy {

	SceneNode npc;
	SceneNode target; //might replace target with position
	Vector3 position;
	float radius;
	//public PatrolPatrolStrategy(SceneNode n, SceneNode t, float r) {
	public PatrolPatrolStrategy(SceneNode n, Vector3 p, float r) {
		npc = n;
		//target = t;
		position = p;
		radius = r;
	}
	
	@Override
	public void move(float deltaTime) {
		//move npc around target at Z = 0
	}
}
