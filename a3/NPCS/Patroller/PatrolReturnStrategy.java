package a3.NPCS.Patroller;

import ray.physics.PhysicsObject;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class PatrolReturnStrategy implements PatrolStrategy {

	SceneNode npc;
	PhysicsObject npcPhys;
	SceneNode target; //might replace target with position
	float radius;
	float power = 10f;
	
	public PatrolReturnStrategy(SceneNode n, SceneNode t, float r) {
		//herefds
		npc = n;
		npcPhys = npc.getPhysicsObject();
		System.out.println("npc patrol: " + npc);
		System.out.println("npcPhys patrol: " + npcPhys);
		System.out.println("the return strategy constructor");
		target = t;
		radius = r;
	}
	
	@Override
	public void move(float deltaTime) {
		//move npc back to the defended node
		//System.out.println("npcPhys: " + npcPhys);
		
		//System.out.println("linear damping: " + npcPhys.getLinearDamping());
		Vector3 start = npc.getWorldPosition();
		Vector3 end = target.getWorldPosition();
		
		float x,y,z;
		x = power * (end.x() - start.x()) * deltaTime;
		y = power * (end.y() - start.y()) * deltaTime;
		z = power * (end.z() - start.z()) * deltaTime;
		npcPhys.applyForce(x, y, z, 0, 0, 0);
	}
}
