package ris;

import java.io.IOException;
import java.util.UUID;

//import a3.myGameEngine.VectorMath;
import ray.physics.PhysicsObject;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import ris.MyGame;

public class GhostAvatar {
	
	MyGame g;
	private UUID id;
	private SceneNode node;
	private Entity entity;
	private SceneNode[] lasers;
	private int team;
	
	public GhostAvatar(MyGame g, UUID id, Vector3 position, Vector3 linear, int team) throws IOException {
		this.g = g;
		this.id = id;
		lasers = g.getNodeMaker().makeGhostLasers();
		this.team = team;
	}
	
	//seters and geters for the id,node,entity, and position.
	
	public void setNode(SceneNode n) {
		node = n;
	} 
	public SceneNode getNode() {return node; }
	
	public UUID getID() { return id; }
	public int getTeam() { return team; }
	
	private Vector3 ahead;
	private PhysicsObject phys;
	public void setPosition(Vector3 v, Vector3 l) {
		phys = node.getPhysicsObject();
		double[] transform = phys.getTransform();
		transform[12] = v.x();
		transform[13] = v.y();
		transform[14] = v.z();
		phys.setTransform(transform);
		
		node.getPhysicsObject().setLinearVelocity(l.toFloatArray());
		
		ahead = node.getWorldPosition().add(Vector3f.createFrom(node.getPhysicsObject().getLinearVelocity()));
		node.lookAt(ahead);
		
		
	}
	
	private float laserSpeed = 400f;
	private int shootCycle = 0;
	public void shoot() {
		PhysicsObject p1 = lasers[shootCycle].getPhysicsObject();
		
		PhysicsObject p2 = lasers[shootCycle+1].getPhysicsObject();
		
		Vector3 up = node.getWorldUpAxis();
		Vector3 right = node.getWorldRightAxis().mult(2);
		Vector3 forward = node.getWorldForwardAxis();
		Vector3 position = node.getWorldPosition().add(forward.mult(10)).sub(up.mult(2));
		
		double[] d1 = p1.getTransform();
		double[] d2 = p2.getTransform();
		
		d1[12] = position.x() + right.x();
		d1[13] = position.y() + right.y();
		d1[14] = position.z() + right.z();
		
		d2[12] = position.x() - right.x();
		d2[13] = position.y() - right.y();
		d2[14] = position.z() - right.z();
		
		p1.setTransform(d1);
		p2.setTransform(d2);
		
		p1.setLinearVelocity(forward.mult(laserSpeed).toFloatArray());
		p2.setLinearVelocity(forward.mult(laserSpeed).toFloatArray());
		
		shootCycle += 2;
		shootCycle%=(lasers.length);
	}
	
	public boolean hitCheck() {
		SceneNode ship = g.getShip();
		for(SceneNode n : lasers) {
			if(VectorMath.distance(ship.getWorldPosition(), n.getWorldPosition()) < 2) {
				return true;
			}
		}
		return false;
	}
}
