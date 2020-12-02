package a3.NPCS.Patroller;

import ray.physics.PhysicsObject;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
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
		System.out.println("PATROL CONSTRUCTOR CHASE ############## " + npc.getPhysicsObject());
	}
	
	@Override
	public void move(float deltaTime) {
		//System.out.println("chase linear damping: " + npcPhys.getLinearDamping());
		
		Vector3 start = npc.getWorldPosition();
		Vector3 end = target.getWorldPosition();
		
		System.out.println("distance: " + VectorMath.distance(start, end));
		
		float x,y,z;
		x = power * (end.x() - start.x()) * deltaTime;
		y = power * (end.y() - start.y()) * deltaTime;
		z = power * (end.z() - start.z()) * deltaTime;
		npcPhys.applyForce(x, y, z, 0, 0, 0);
	}
}
