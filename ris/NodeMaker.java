package ris;

import java.io.IOException;

import ris.MyGame;
import ray.physics.PhysicsEngine;
import ray.physics.PhysicsObject;
import ray.rage.Engine;
import ray.rage.asset.material.Material;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class NodeMaker {

	private SceneManager sm;
	private Engine eng;
	private PhysicsEngine physics;
	private SceneNode ship;
	
	public NodeMaker(Engine e, SceneManager s, PhysicsEngine physics) {
		sm = s;
		eng = e;
		this.physics = physics;
		ship = sm.getSceneNode("shipNode");
	}
	
	public SceneNode[] makeLasers() throws IOException {
		int laserCount = 16;
		SceneNode[] list = new SceneNode[laserCount];
		
		for(int i=0; i<list.length; i++) {
			String s = "playerLasers" + Integer.toString(i);
			list[i] = makeLaser(s);
		}
		
		return list;
	}
	
	public SceneNode[] makeGhostLasers() throws IOException {
		
		int laserCount = 16;
		SceneNode[] list = new SceneNode[laserCount];
		
		for(int i=0; i<list.length; i++) {
			String s = "ghostLasers" + Integer.toString(i);
			list[i] = makeLaser(s);
		}
		
		return list;
	}
	
	public SceneNode[] makeNPCLasers(String name) throws IOException {
		int laserCount = 4;
		SceneNode[] list = new SceneNode[laserCount];
		
		for(int i=0; i<list.length; i++) {
			String s = name + "npcLasers" + Integer.toString(i);
			list[i] = makeLaser(s);
		}
		
		return list;
	}
	
	public SceneNode makeLaser(String name) throws IOException {
		
		SceneNode ln = sm.getRootSceneNode().createChildSceneNode(name);
		
		Vector3 position = Vector3f.createFrom(10000,10000,10000);
		ln.setLocalPosition(position);
		
		Entity le = sm.createEntity(name + "laser", "otherLaser.obj");

		le.setPrimitive(Primitive.TRIANGLES);
		
		ln.attachObject(le);
		
		float scale = 0.5f;
		
		ln.setLocalScale(Vector3f.createFrom(scale,scale,scale));
		
		float mass = 1.0f;
		//float up[] = {0,1,0};
		double[] temptf;
		
		temptf = MyGame.toDoubleArray(ln.getLocalTransform().toFloatArray());
		PhysicsObject shipPhysicsObject = physics.addSphereObject(physics.nextUID(), mass, temptf, 1.0f);
		shipPhysicsObject.setDamping(0, 0);
		ln.setPhysicsObject(shipPhysicsObject);
		
		ln.lookAt(ship);
		
		
		return ln;
	}
	
	private Vector3 throttleOffset;
	private float throttleGap = 0.07f;
	public SceneNode[] makeThrottleIndicators() throws IOException {
		SceneNode[] theHud = new SceneNode[10];
		
		for(int i=0; i<10; i++) {
			theHud[i] = makeThrottleIndicator("throttleIndicator" + i);
		}
		
		for(int i=0;i<10;i++) {
			theHud[i].setLocalPosition(-1 * i*throttleGap + (throttleGap*10/2),0,0);
			
		}
		return theHud;
	}
	
	private SceneNode makeThrottleIndicator(String name) throws IOException {
		
		SceneNode ti = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity tie = sm.createEntity(name, "throttleIndicator.obj");
		tie.setPrimitive(Primitive.TRIANGLES);
		
		ti.attachObject(tie);
		
		float scale = 0.08f;
		
		ti.setLocalScale(Vector3f.createFrom(scale,scale,scale));
		
		ship.attachChild(ti);
		
		ti.pitch(Degreef.createFrom(270));
		
		return ti;
	}
	
	float scoreGap = 0.15f;
	public SceneNode[] makeScoreIndicators() throws IOException {
		SceneNode[] theHud = new SceneNode[4];
		
		for(int i=0; i<theHud.length; i++) {
			theHud[i] = makeScoreIndicator("scoreIndicator" + i);
		}
		
		for(int i=0;i<theHud.length;i++) {
			theHud[i].setLocalPosition(-1 * i*scoreGap + (scoreGap*theHud.length/2),0,0);
		}
		return theHud;
	}
	
	private SceneNode makeScoreIndicator(String name) throws IOException {
		
		SceneNode ti = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity tie = sm.createEntity(name, "scoreIndicator.obj");
		tie.setPrimitive(Primitive.TRIANGLES);
		
		ti.attachObject(tie);
		
		float scale = 0.06f;
		
		ti.setLocalScale(Vector3f.createFrom(scale,scale,scale));
		
		ship.attachChild(ti);
		
		ti.pitch(Degreef.createFrom(270));
		
		return ti;
	}
	
	public SceneNode makeNPC(String name, Vector3 position) throws IOException {
		SceneNode npc = sm.getRootSceneNode().createChildSceneNode(name);
		
		Entity e = sm.createEntity(name + "i", "DropShipVer4.obj");
		e.setPrimitive(Primitive.TRIANGLES);
		
		npc.attachObject(e);
		
		npc.setLocalPosition(position);
		
		float mass = 1.0f;
		//float up[] = {0,1,0};
		double[] temptf;
		
		temptf = MyGame.toDoubleArray(npc.getLocalTransform().toFloatArray());
		PhysicsObject shipPhysicsObject = physics.addSphereObject(physics.nextUID(), mass, temptf, 1.0f);
		shipPhysicsObject.setDamping(0, 0);
		npc.setPhysicsObject(shipPhysicsObject);
		
		return npc;
	}
}
