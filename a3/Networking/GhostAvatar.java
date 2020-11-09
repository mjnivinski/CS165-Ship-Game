package a3.Networking;

import java.util.UUID;

import ray.rage.scene.Entity;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class GhostAvatar {
	private UUID id;
	private SceneNode node;
	private Entity entity;
	
	public GhostAvatar(UUID id, Vector3 position) {
		this.id = id;
	}
	
	//seters and geters for the id,node,entity, and position.
	
	public void setNode(SceneNode n) { node = n; } 
	public SceneNode getNode() { return node; }
	
	public UUID getID() { return id; }
	
	public void setPosition(Vector3 v) {
		node.setLocalPosition(v);
	}
}
