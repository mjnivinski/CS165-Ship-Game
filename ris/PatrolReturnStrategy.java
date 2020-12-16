package ris;

import ray.physics.PhysicsObject;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class PatrolReturnStrategy implements PatrolStrategy {

	SceneNode npc;
	PhysicsObject npcPhys;
	SceneNode target; //might replace target with position
	float radius;
	float power = 10f;
	Vector3 position;
	
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
	
	public PatrolReturnStrategy(SceneNode n, Vector3 p) {
		npc = n;
		position = p;
		npcPhys = n.getPhysicsObject();
	}
	
	@Override
	public void move(float deltaTime) {
		//move npc back to the defended node
		//System.out.println("npcPhys: " + npcPhys);
		
		//System.out.println("linear damping: " + npcPhys.getLinearDamping());
		Vector3 start = npc.getWorldPosition();
		Vector3 end = position;
		//Vector3 end = target.getWorldPosition();
		
		float[] xyz = new float[3];
		xyz[0] = end.x() - start.x();
		xyz[1] = end.y() - start.y();
		xyz[2] = end.z() - start.z();
		npcPhys.setLinearVelocity(xyz);
		
		/*
		float x,y,z;
		x = power * (end.x() - start.x()) * deltaTime;
		y = power * (end.y() - start.y()) * deltaTime;
		z = power * (end.z() - start.z()) * deltaTime;
		npcPhys.applyForce(x, y, z, 0, 0, 0);*/
	}
}
