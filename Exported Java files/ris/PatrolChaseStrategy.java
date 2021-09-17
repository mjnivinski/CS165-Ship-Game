package ris;

import ray.physics.PhysicsObject;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

import java.util.Arrays;

//import a3.myGameEngine.VectorMath;

public class PatrolChaseStrategy implements PatrolStrategy {

	private PatrolEnemy patrolEnemy;
	private SceneNode npc;
	private SceneNode target;
	private PhysicsObject npcPhys;
	private float power = 10f;
	
	SceneNode[] lasers;
	
	private float chaseSpeed = 0.05f;
	float hitRange = 3f;
	
	public PatrolChaseStrategy(PatrolEnemy pe, SceneNode n, SceneNode t, SceneNode[] ls) {
		patrolEnemy = pe;
		npc = n;
		target = t;
		npcPhys = npc.getPhysicsObject();
		lasers = ls;
	}
	
	@Override
	public void move(float deltaTime) {
		System.out.println("CHASE MOVING");
		
		Vector3 start = npc.getWorldPosition();
		Vector3 end = target.getWorldPosition();
		
		Vector3 direction = end.sub(start);
		
		direction = direction.mult(chaseSpeed);
		
		npcPhys.setLinearVelocity(direction.toFloatArray());
		
		shooting(deltaTime);
		laserCheck();
	}
	
	private int shootCycle = 0;
	float timeSinceLastShot = 0;
	float fireRate = 1.0f;
	
	private void shooting(float deltaTime) {
		timeSinceLastShot += deltaTime;
		if(timeSinceLastShot > 1/fireRate) {
			shoot();
		}
	}
	
	private float laserSpeed = 300;
	
	private void shoot() {
	
		
		timeSinceLastShot = 0;
		
		PhysicsObject p1 = lasers[shootCycle].getPhysicsObject();
		
		Vector3 to = target.getWorldPosition();
		Vector3 from = npc.getWorldPosition();
		Vector3 start = to.sub(from).normalize();
		Vector3 forward = start;
		
		double[] d1 = p1.getTransform();
		
		d1[12] = start.x() + from.x();
		d1[13] = start.y() + from.y();
		d1[14] = start.z() + from.z();
		
		p1.setTransform(d1);
		
		p1.setLinearVelocity(forward.mult(laserSpeed).toFloatArray());
		
		shootCycle += 1;
		shootCycle%=(lasers.length);
	}
	
	//checks for collision between npc laser and player ship
	private void laserCheck() {
		for(SceneNode l : lasers) {
			if(VectorMath.distance(l.getWorldPosition(), target.getWorldPosition()) < hitRange) {
				patrolEnemy.npcHit();
				break;
			}
		}
	}
}
