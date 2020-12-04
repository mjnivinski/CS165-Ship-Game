package a3.NPCS.Patroller;

import ray.physics.PhysicsObject;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

import java.util.Arrays;

import a3.myGameEngine.VectorMath;

public class PatrolChaseStrategy implements PatrolStrategy {

	private SceneNode npc;
	private SceneNode target;
	private PhysicsObject npcPhys;
	private float power = 10f;
	
	public PatrolChaseStrategy(SceneNode n, SceneNode t) {
		npc = n;
		target = t;
		npcPhys = npc.getPhysicsObject();
	}
	
	@Override
	public void move(float deltaTime) {
		//System.out.println("chase linear damping: " + npcPhys.getLinearDamping());
		
		Vector3 start = npc.getWorldPosition();
		Vector3 end = target.getWorldPosition();
		
		//System.out.println("distance: " + VectorMath.distance(start, end));
		
		
		float[] xyz = new float[3];
		xyz[0] = end.x() - start.x();
		xyz[1] = end.y() - start.y();
		xyz[2] = end.z() - start.z();
		npcPhys.setLinearVelocity(xyz);
		/*
		x = power * (end.x() - start.x()) * deltaTime;
		y = power * (end.y() - start.y()) * deltaTime;
		z = power * (end.z() - start.z()) * deltaTime;
		npcPhys.applyForce(x, y, z, 0, 0, 0);*/
		
		//System.out.println("get linear velocity: " + Arrays.toString(npcPhys.getLinearVelocity()));
	}
}
